package com.sparta.blackwhitedeliverydriver.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sparta.blackwhitedeliverydriver.dto.BasketResponse;
import com.sparta.blackwhitedeliverydriver.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Basket;
import com.sparta.blackwhitedeliverydriver.repository.BasketRepository;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BasketServiceTest {
    BasketService basketService;
    BasketRepository basketRepository = mock(BasketRepository.class);

    @BeforeEach
    public void setUp() {
        basketService = new BasketService(basketRepository);
    }

    @Test
    @DisplayName("장바구니 담기 성공")
    void addProductToBasket_success() {
        //given
        BasketResponse request = BasketResponse.builder().userId(1L)
                .productId("f47ac10b-58cc-4372-a567-0e02b2c3d479")
                .quantity(2)
                .build();

        given(basketRepository.save(any())).willReturn(
                Basket.builder().basketId(UUID.fromString("7c9f6b72-4d8e-49b0-9b6e-7fc8f0e905d9"))
                        .userId(1L)
                        .productId(UUID.fromString(request.getProductId()))
                        .quantity(request.getQuantity()).build());

        //when
        BasketResponseDto response = basketService.addProductToBasket(request);

        //then
        Assertions.assertEquals(UUID.fromString("7c9f6b72-4d8e-49b0-9b6e-7fc8f0e905d9"), response.getBasketId());
    }
}