package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.OrderGetDetailResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.entity.Basket;
import com.sparta.blackwhitedeliverydriver.entity.Order;
import com.sparta.blackwhitedeliverydriver.entity.OrderProduct;
import com.sparta.blackwhitedeliverydriver.entity.OrderStatusEnum;
import com.sparta.blackwhitedeliverydriver.entity.Product;
import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.exception.BasketExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.OrderExceptionMessage;
import com.sparta.blackwhitedeliverydriver.repository.BasketRepository;
import com.sparta.blackwhitedeliverydriver.repository.OrderProductRepository;
import com.sparta.blackwhitedeliverydriver.repository.OrderRepository;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final BasketRepository basketRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponseDto createOrder(String username) {
        //유저 유효성 검사
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        //유저와 관련된 장바구니 품목 찾기
        List<Basket> baskets = basketRepository.findAllByUser(user);

        //장바구니 개수 체크
        checkBasketCount(baskets);

        //order 엔티티 생성 및 저장
        Store store = baskets.get(0).getStore();
        Order order = Order.ofUserAndStore(user, store);
        order = orderRepository.save(order);

        //연관관계 테이블에 장바구니 품목 저장
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (Basket basket : baskets) {
            Product product = basket.getProduct();
            OrderProduct orderProduct = OrderProduct.of(basket, product, order);
            orderProducts.add(orderProduct);
        }
        orderProductRepository.saveAll(orderProducts);

        //장바구니 삭제
        basketRepository.deleteAll(baskets);

        //최종금액 계산 및 업데이트
        int price = calculateFinalPay(orderProducts);
        order.updateFinalPay(price);
        orderRepository.save(order);

        return new OrderResponseDto(order.getId());
    }

    public OrderGetDetailResponseDto getOrderDetail(String username, UUID orderId) {
        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        //주문 유효성
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));

        // CUSTOMER인 경우 Order의 user인지 체크
        if (user.getRole().equals(UserRoleEnum.CUSTOMER)) {
            checkOrderUser(order, user);
        }

        // Product Entity 구현되면 음식 목록도 포함하여 리턴
        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrder(order);

        return OrderGetDetailResponseDto.of(order, orderProducts);
    }

    public List<OrderGetResponseDto> getOrders(String username) {
        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        //주문 조회
        UserRoleEnum role = user.getRole();
        List<Order> orders;
        if (role.equals(UserRoleEnum.CUSTOMER)) {
            orders = orderRepository.findAllByUser(user);
        } else {
            orders = orderRepository.findAll();
        }

        return orders.stream().map(OrderGetResponseDto::fromOrder).collect(Collectors.toList());
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(String username, OrderUpdateRequestDto request) {
        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        //주문 유효성
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));

        //주문의 점포 주인과 유저 체크
        checkStoreOwnerEquals(order.getStore(), user);

        order.updateStatus(request.getStatus());
        return new OrderResponseDto(order.getId());
    }

    @Transactional
    public OrderResponseDto deleteOrder(String username, UUID orderId) {
        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        //주문 유효성
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));

        //주문 유저 와 API 호출한 유저 체크
        checkOrderUser(order, user);

        //주문 상태 확인
        checkEnableDeleteOrderStatus(order);

        //orderProduct 조회 후 basket 저장
        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrder(order);
        for (OrderProduct orderProduct : orderProducts) {
            Basket basket = Basket.ofUserAndOrderProduct(user, orderProduct.getProduct(), orderProduct); // 수정이 필요
            basketRepository.save(basket);
        }

        //orderProduct 삭제
        orderProductRepository.deleteAll(orderProducts);

        //order 삭제
        orderRepository.delete(order);
        return new OrderResponseDto(order.getId());
    }

    private void checkBasketCount(List<Basket> baskets) {
        if (baskets.isEmpty()) {
            throw new IllegalArgumentException(BasketExceptionMessage.BASKET_COUNT_ZERO.getMessage());
        }
    }

    private int calculateFinalPay(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .mapToInt(orderProduct -> orderProduct.getPrice() * orderProduct.getQuantity())
                .sum();
    }

    private void checkOrderUser(Order order, User user) {
        String orderUsername = order.getUser().getUsername();
        String username = user.getUsername();
        if (!orderUsername.equals(username)) {
            throw new IllegalArgumentException(OrderExceptionMessage.ORDER_USER_NOT_EQUALS.getMessage());
        }
    }

    private void checkEnableDeleteOrderStatus(Order order) {
        if (!order.getStatus().equals(OrderStatusEnum.CREATE) && !order.getStatus().equals(OrderStatusEnum.PENDING)) {
            throw new IllegalArgumentException(OrderExceptionMessage.ORDER_UNABLE_DELETE_STATUS.getMessage());
        }
    }

    private void checkStoreOwnerEquals(Store store, User user) {
        User owner = store.getUser();
        if (!owner.getUsername().equals(user.getUsername())) {
            throw new IllegalArgumentException("점포 오너 권한이 없습니다.");
        }
    }

}
