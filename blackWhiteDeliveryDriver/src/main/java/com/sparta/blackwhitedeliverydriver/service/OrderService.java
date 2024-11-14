package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.OrderResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Basket;
import com.sparta.blackwhitedeliverydriver.entity.Order;
import com.sparta.blackwhitedeliverydriver.entity.OrderProduct;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.exception.BasketExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.repository.BasketRepository;
import com.sparta.blackwhitedeliverydriver.repository.OrderProductRepository;
import com.sparta.blackwhitedeliverydriver.repository.OrderRepository;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final BasketRepository basketRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponseDto createOrder(String username) { // 메서드를 어떻게 개선할 수 있을까요...? 좋은 의견 남겨주시면 감사하겠습니다...
        //유저 유효성 검사
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        //유저와 관련된 장바구니 품목 찾기
        List<Basket> baskets = basketRepository.findAllByUser(user);
        //장바구니 개수 체크
        checkBasketCount(baskets);
        //order 엔티티 생성 및 저장
        Order order = Order.fromUser(user);
        order = orderRepository.save(order);
        //연관관계 테이블에 장바구니 품목 저장
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (Basket basket : baskets) {
            OrderProduct orderProduct = OrderProduct.ofBasketAndOrder(basket, order);
            orderProducts.add(orderProduct);
        }
        orderProductRepository.saveAll(orderProducts);
        //장바구니 삭제
        basketRepository.deleteAll(baskets);
        //최종금액 계산 및 업데이트
        int price = calculateFinalPay(orderProducts);
        order.updateFinalPay(price);
        orderRepository.save(order);

        return OrderResponseDto.from(order);
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
}
