package com.sparta.blackwhitedeliverydriver.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sparta.blackwhitedeliverydriver.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketRemoveRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Basket;
import com.sparta.blackwhitedeliverydriver.repository.BasketRepository;
import java.util.Optional;
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
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId("f47ac10b-58cc-4372-a567-0e02b2c3d479")
                .quantity(2)
                .build();

        given(basketRepository.save(any())).willReturn(
                Basket.builder().basketId(UUID.fromString("7c9f6b72-4d8e-49b0-9b6e-7fc8f0e905d9"))
                        .productId(UUID.fromString(request.getProductId()))
                        .quantity(request.getQuantity()).build());

        //when
        BasketResponseDto response = basketService.addProductToBasket(request);

        //then
        Assertions.assertEquals(UUID.fromString("7c9f6b72-4d8e-49b0-9b6e-7fc8f0e905d9"), response.getBasketId());
    }

    @Test
    @DisplayName("장바구니 빼기 성공")
    void removeProductFromBasket_success() {
        //given
        BasketRemoveRequestDto request = BasketRemoveRequestDto.builder()
                .basketId(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"))
                .build();
        Basket basket = Basket.builder()
                .basketId(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"))
                .productId(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"))
                .quantity(2)
                .build();

        given(basketRepository.findById(any())).willReturn(Optional.ofNullable(basket));

        //when
        doNothing().when(basketRepository).delete(any()); //repository delete 건너뛰기
        BasketResponseDto response = basketService.removeProductFromBasket(request);

        //then
        Assertions.assertEquals(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"), response.getBasketId());
    }


    @Test
    @DisplayName("장바구니 빼기 실패 : 장바구니 존재하지 않는 경우")
    void removeProductFromBasket_fail1() {
        //given
        BasketRemoveRequestDto request = BasketRemoveRequestDto.builder()
                .basketId(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"))
                .build();

        //when
        when(basketRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            basketService.removeProductFromBasket(request);
        });
    }
}