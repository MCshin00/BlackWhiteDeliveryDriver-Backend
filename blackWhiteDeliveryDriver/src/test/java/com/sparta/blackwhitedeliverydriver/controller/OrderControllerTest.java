package com.sparta.blackwhitedeliverydriver.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blackwhitedeliverydriver.dto.OrderGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.mock.user.MockUser;
import com.sparta.blackwhitedeliverydriver.service.OrderService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    OrderService orderService;
    @Autowired
    ObjectMapper mapper;

    private static final String BASE_URL = "/api/v1";

    @Test
    @DisplayName("주문 생성하기")
    @MockUser
    void createOrder() throws Exception {
        //given
        String orderId = "3e678a43-41b1-4a35-97fb-4ad686308074";
        OrderResponseDto response = new OrderResponseDto(UUID.fromString(orderId));
        //when
        when(orderService.createOrder(any())).thenReturn(response);
        //then
        mvc.perform(post(BASE_URL + "/orders")
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists());

    }

    @Test
    @DisplayName("주문 상세 조회하기")
    @MockUser(role = UserRoleEnum.MASTER)
    void getOrderDetail() throws Exception {
        //given
        String orderId = "3e678a43-41b1-4a35-97fb-4ad686308074";
        String username = "user1";
        OrderGetResponseDto response = OrderGetResponseDto.builder()
                .orderId(UUID.fromString(orderId))
                .username(username)
                .finalPay(10000)
                .discountAmount(0)
                .discountRate(0)
                .build();

        //when
        when(orderService.getOrderDetail(any(), any())).thenReturn(response);

        //then
        mvc.perform(get(BASE_URL + "/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.username").exists());
    }

    @Test
    @DisplayName("주문 목록 조회하기")
    @MockUser
    void getOrders() throws Exception {
        //given
        String orderId = "3e678a43-41b1-4a35-97fb-4ad686308074";
        String username = "user1";
        OrderGetResponseDto response = OrderGetResponseDto.builder()
                .orderId(UUID.fromString(orderId))
                .username(username)
                .finalPay(10000)
                .discountAmount(0)
                .discountRate(0)
                .build();
        List<OrderGetResponseDto> responseList = List.of(response);

        //when
        when(orderService.getOrders(any())).thenReturn(responseList);

        //then
        mvc.perform(get(BASE_URL + "/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*.orderId").exists())
                .andExpect(jsonPath("$.*.username").exists());

    }
}