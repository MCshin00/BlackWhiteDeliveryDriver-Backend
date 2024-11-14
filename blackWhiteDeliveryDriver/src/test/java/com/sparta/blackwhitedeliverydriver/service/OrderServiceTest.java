package com.sparta.blackwhitedeliverydriver.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sparta.blackwhitedeliverydriver.dto.OrderResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Basket;
import com.sparta.blackwhitedeliverydriver.entity.Order;
import com.sparta.blackwhitedeliverydriver.entity.OrderProduct;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.repository.BasketRepository;
import com.sparta.blackwhitedeliverydriver.repository.OrderProductRepository;
import com.sparta.blackwhitedeliverydriver.repository.OrderRepository;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderServiceTest {
    OrderService orderService;

    BasketRepository basketRepository = mock(BasketRepository.class);
    OrderRepository orderRepository = mock(OrderRepository.class);
    OrderProductRepository orderProductRepository = mock(OrderProductRepository.class);
    UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    public void setUp() {
        orderService = new OrderService(basketRepository, orderRepository, orderProductRepository, userRepository);
    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder() {
        //given
        String username = "user1";
        String basketId = "0eba37e5-a3bc-4053-bef6-2e087cf1e227";
        String orderId = "2b6ad274-8f98-44c2-9321-ea0de46b3ec6";
        String productId = "e7521693-c495-4699-9bc2-7c70d731d214";
        String orderProductId = "1e217cbf-e3ec-4e85-9f9b-42557c1dd079";
        User user = User.builder()
                .username(username)
                .role(UserRoleEnum.CUSTOMER)
                .build();
        Basket basket = Basket.builder()
                .id(UUID.fromString(basketId))
                .user(user)
                .productId(UUID.fromString(productId))
                .quantity(2)
                .build();
        Order order = Order.builder()
                .id(UUID.fromString(orderId))
                .user(user)
                .build();
        OrderProduct orderProduct = OrderProduct.builder()
                .id(UUID.fromString(orderProductId))
                .order(order)
                .product(UUID.fromString(productId))
                .quantity(2)
                .price(5000)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(basketRepository.findAllByUser(any())).willReturn(List.of(basket));
        given(orderRepository.save(any())).willReturn(order);
        when(orderProductRepository.saveAll(any())).thenReturn(List.of(orderProduct));
        doNothing().when(basketRepository).deleteAll(any());
        //when
        OrderResponseDto response = orderService.createOrder(username);
        //then
        Assertions.assertEquals(10000, order.getFinalPay());
        Assertions.assertEquals(orderId, response.getOrderId().toString());
    }

    @Test
    @DisplayName("주문 생성 실패1 : 유저가 없는 경우")
    void createOrder_fail1() {
        //given
        String username = "user1";
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        assertThrows(NullPointerException.class, () -> orderService.createOrder(username));
    }

    @Test
    @DisplayName("주문 생성 실패2 : 상품이 존재하지 않는 경우")
    void createOrder_fail2() {
        // Product 엔티티 생성 후 구현 예정
    }

    @Test
    @DisplayName("주문 생성 실패3 : 장바구니가 존재하지 않는 경우")
    void createOrder_fail3() {
        //given
        String username = "user1";
        User user = User.builder()
                .username(username)
                .role(UserRoleEnum.CUSTOMER)
                .build();
        List<Basket> baskets = new ArrayList<>();
        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        when(basketRepository.findAllById(any())).thenReturn(baskets);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(username));
    }
}