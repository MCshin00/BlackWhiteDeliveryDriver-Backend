package com.sparta.blackwhitedeliverydriver.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.sparta.blackwhitedeliverydriver.entity.Basket;
import com.sparta.blackwhitedeliverydriver.entity.Order;
import com.sparta.blackwhitedeliverydriver.repository.BasketRepository;
import com.sparta.blackwhitedeliverydriver.repository.OrderProductRepository;
import com.sparta.blackwhitedeliverydriver.repository.OrderRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderServiceTest {
    OrderService orderService;

    BasketRepository basketRepository = mock(BasketRepository.class);
    OrderRepository orderRepository = mock(OrderRepository.class);
    OrderProductRepository orderProductRepository = mock(OrderProductRepository.class);

    @BeforeEach
    public void setUp(){
        orderService = new OrderService(basketRepository, orderRepository, orderProductRepository);
    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder() {
        //given
        String username = "user1";
        String orderId = "2b6ad274-8f98-44c2-9321-ea0de46b3ec6";
        String basketId1 = "0eba37e5-a3bc-4053-bef6-2e087cf1e227";
        String basketId2 = "a4fa30ab-e662-46ef-a95e-a9fb426835ae";
        String productId1 = "e7521693-c495-4699-9bc2-7c70d731d214";
        String productId2 = "fe1789bd-ef1f-45d7-9bda-a069ac718fd5";
        Order order = Order.builder()
                .id(UUID.fromString(orderId))
                .user(username)
                .build();
        Basket basket1 = Basket.builder()
                .id(UUID.fromString(basketId1))
                .productId(UUID.fromString(productId1))
                .build();
        Basket basket2 = Basket.builder()
                .id(UUID.fromString(basketId2))
                .productId(UUID.fromString(productId2))
                .build();
        //when

        //then
    }
}