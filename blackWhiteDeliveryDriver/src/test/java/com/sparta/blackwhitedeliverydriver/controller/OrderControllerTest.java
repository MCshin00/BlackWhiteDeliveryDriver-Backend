package com.sparta.blackwhitedeliverydriver.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blackwhitedeliverydriver.config.TestSecurityConfig;
import com.sparta.blackwhitedeliverydriver.dto.OrderGetDetailResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.entity.OrderStatusEnum;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@Import(TestSecurityConfig.class)
class OrderControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    OrderService orderService;
    @Autowired
    ObjectMapper mapper;

    private static final String BASE_URL = "/api/v1";

    @Test
    @DisplayName("주문 생성하기 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void createOrder_success() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();
        OrderResponseDto response = new OrderResponseDto(orderId);

        //when
        when(orderService.createOrder(any())).thenReturn(response);

        //then
        mvc.perform(post(BASE_URL + "/orders"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists());

    }

    @Test
    @DisplayName("주문 생성하기 실패 : 허용하지 않는 권한")
    @MockUser(role = UserRoleEnum.OWNER)
    void createOrder_fail() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();
        OrderResponseDto response = new OrderResponseDto(orderId);

        //when
        when(orderService.createOrder(any())).thenReturn(response);

        //then
        mvc.perform(post(BASE_URL + "/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("주문 상세 조회하기 성공")
    @MockUser(role = UserRoleEnum.MASTER)
    void getOrderDetail_success() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();
        String username = "user";
        OrderGetDetailResponseDto response = OrderGetDetailResponseDto.builder()
                .orderId(orderId)
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
    @DisplayName("주문 상세 조회하기 실패 : 허용하지 않은 권한")
    @MockUser(role = UserRoleEnum.OWNER)
    void getOrderDetail() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();
        String username = "user";
        OrderGetDetailResponseDto response = OrderGetDetailResponseDto.builder()
                .orderId(orderId)
                .username(username)
                .finalPay(10000)
                .discountAmount(0)
                .discountRate(0)
                .build();

        //when
        when(orderService.getOrderDetail(any(), any())).thenReturn(response);

        //then
        mvc.perform(get(BASE_URL + "/orders/{orderId}", orderId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("주문 목록 조회하기 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void getOrders_success() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();
        String username = "user";
        OrderGetResponseDto response = OrderGetResponseDto.builder()
                .orderId(orderId)
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

    @Test
    @DisplayName("주문 목록 조회하기 실패")
    @MockUser(role = UserRoleEnum.OWNER)
    void getOrders_fail() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();
        String username = "user";
        OrderGetResponseDto response = OrderGetResponseDto.builder()
                .orderId(orderId)
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
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("주문 상태 수정하기 성공")
    @MockUser(role = UserRoleEnum.OWNER)
    void updateOrderStatus_success() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(orderId, OrderStatusEnum.ACCEPTED);
        OrderResponseDto response = new OrderResponseDto(orderId);

        //when
        when(orderService.updateOrderStatus(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);

        mvc.perform(put(BASE_URL + "/orders")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists());

    }

    @Test
    @DisplayName("주문 상태 수정하기 실패")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void updateOrderStatus_fail() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(orderId, OrderStatusEnum.ACCEPTED);
        OrderResponseDto response = new OrderResponseDto(orderId);

        //when
        when(orderService.updateOrderStatus(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);

        mvc.perform(put(BASE_URL + "/orders")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("주문 취소하기 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void deleteOrder_success() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();
        OrderResponseDto response = new OrderResponseDto(orderId);

        //when
        when(orderService.deleteOrder(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/orders/{orderId}", orderId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists());
    }

    @Test
    @DisplayName("주문 취소하기 실패 : 허용하지 않는 권한")
    @MockUser(role = UserRoleEnum.OWNER)
    void deleteOrder_fail() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();
        OrderResponseDto response = new OrderResponseDto(orderId);

        //when
        when(orderService.deleteOrder(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/orders/{orderId}", orderId.toString()))
                .andExpect(status().isForbidden());
    }
}