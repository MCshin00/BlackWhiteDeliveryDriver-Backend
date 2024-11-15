package com.sparta.blackwhitedeliverydriver.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blackwhitedeliverydriver.config.TestSecurityConfig;
import com.sparta.blackwhitedeliverydriver.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.mock.user.MockUser;
import com.sparta.blackwhitedeliverydriver.service.BasketService;
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

@WebMvcTest(BasketController.class)
@Import(TestSecurityConfig.class)
class BasketControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BasketService basketService;

    @Autowired
    ObjectMapper mapper;

    private static final String BASE_URL = "/api/v1";

    @Test
    @DisplayName("장바구니 담기 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void addProductToBasket_success() throws Exception {
        //given
        UUID productId = UUID.randomUUID();
        UUID basketId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        int quantity = 2;
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(productId)
                .storeId(storeId)
                .quantity(quantity)
                .build();
        BasketResponseDto response = new BasketResponseDto(basketId);

        //when
        when(basketService.addProductToBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);
        mvc.perform(post(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.basketId").exists());
    }

    @Test
    @DisplayName("장바구니 담기 실패 : 권한이 CUSTOMER 아닌 경우")
    @MockUser(role = UserRoleEnum.OWNER)
    void addProductToBasket_fail() throws Exception {
        //given
        UUID productId = UUID.randomUUID();
        UUID basketId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        int quantity = 2;
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(productId)
                .storeId(storeId)
                .quantity(quantity)
                .build();
        BasketResponseDto response = new BasketResponseDto(basketId);

        //when
        when(basketService.addProductToBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);
        mvc.perform(post(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장바구니 빼기 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void removeProductFromBasket_success() throws Exception {
        //given
        UUID basketId = UUID.randomUUID();
        BasketResponseDto response = new BasketResponseDto(basketId);

        //when
        when(basketService.removeProductFromBasket(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/baskets/{basketId}", basketId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId").exists());
    }

    @Test
    @DisplayName("장바구니 빼기 실패 : 허용하지 않은 권한으로 호출")
    @MockUser(role = UserRoleEnum.OWNER)
    void removeProductFromBasket_fail() throws Exception {
        //given
        UUID basketId = UUID.randomUUID();
        BasketResponseDto response = new BasketResponseDto(basketId);

        //when
        when(basketService.removeProductFromBasket(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/baskets/{basketId}", basketId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장바구니 조회 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void getBaskets_success() throws Exception {
        //given
        UUID basketId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        String storeName = "storeName";
        Integer quantity = 2;
        BasketGetResponseDto responseDto = BasketGetResponseDto.builder()
                .basketId(basketId)
                .productId(productId)
                .storeName(storeName)
                .storeId(storeId)
                .quantity(quantity)
                .build();
        //when
        when(basketService.getBaskets(any())).thenReturn(List.of(responseDto));

        //then
        mvc.perform(get(BASE_URL + "/baskets"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 조회 실패 : 허용하지 않은 권한")
    @MockUser(role = UserRoleEnum.OWNER)
    void getBaskets_fail() throws Exception {
        //given
        UUID basketId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        String storeName = "storeName";
        Integer quantity = 2;
        BasketGetResponseDto responseDto = BasketGetResponseDto.builder()
                .basketId(basketId)
                .productId(productId)
                .storeName(storeName)
                .storeId(storeId)
                .quantity(quantity)
                .build();
        //when
        when(basketService.getBaskets(any())).thenReturn(List.of(responseDto));

        //then
        mvc.perform(get(BASE_URL + "/baskets"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장바구니 수정 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void updateBasket_success() throws Exception {
        //given
        UUID basketId = UUID.randomUUID();
        int quantity = 2;
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(basketId)
                .build();
        //when
        when(basketService.updateBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(BasketUpdateRequestDto.builder()
                .basketId(basketId)
                .quantity(quantity)
                .build());

        mvc.perform(put(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId").exists());
    }

    @Test
    @DisplayName("장바구니 수정 실패 : 허용하지 않은 권한")
    @MockUser(role = UserRoleEnum.OWNER)
    void updateBasket_fail() throws Exception {
        //given
        UUID basketId = UUID.randomUUID();
        int quantity = 2;

        //when
        when(basketService.updateBasket(any(), any()))
                .thenReturn(BasketResponseDto.builder()
                        .basketId(basketId)
                        .build());

        //then
        String body = mapper.writeValueAsString(BasketUpdateRequestDto.builder()
                .basketId(basketId)
                .quantity(quantity)
                .build());

        mvc.perform(put(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}