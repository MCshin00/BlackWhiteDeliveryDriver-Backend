package com.sparta.blackwhitedeliverydriver.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blackwhitedeliverydriver.dto.BasketRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.service.BasketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BasketController.class)
class BasketControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BasketService basketService;

    @Autowired
    ObjectMapper mapper;

    private static final String BASE_URL = "/api/v1";

    @Test
    @DisplayName("장바구니 담기")
    void addProductToBasket() throws Exception {
        //give
        Long userId = 1L;
        String productId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        int quantity = 2;

        //when
        when(basketService.addProductToBasket(any())).thenReturn(BasketResponseDto.builder()
                .basketId("e623f3c2-4b79-4f3a-b876-9d1b5d47a283")
                .build());

        //then
        String body = mapper.writeValueAsString(BasketRequestDto.builder()
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .build());
        mvc.perform(post(BASE_URL + "/basket")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId").exists());
    }
}