package com.sparta.blackwhitedeliverydriver.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sparta.blackwhitedeliverydriver.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketRemoveRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.entity.Basket;
import com.sparta.blackwhitedeliverydriver.repository.BasketRepository;
import java.util.List;
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
        String basketId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        String productId = "1e217cbf-e3ec-4e85-9f9b-42557c1dd079";
        Basket basket = Basket.builder()
                .basketId(UUID.fromString(basketId))
                .productId(UUID.fromString(productId))
                .quantity(2)
                .build();

        given(basketRepository.findById(any())).willReturn(Optional.ofNullable(basket));

        //when
        doNothing().when(basketRepository).delete(any()); //repository delete 건너뛰기
        BasketResponseDto response = basketService.removeProductFromBasket(basketId);

        //then
        Assertions.assertEquals(UUID.fromString(basketId), response.getBasketId());
    }


    @Test
    @DisplayName("장바구니 빼기 실패 : 장바구니 존재하지 않는 경우")
    void removeProductFromBasket_fail1() {
        //given
        String basketId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";

        //when
        when(basketRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            basketService.removeProductFromBasket(basketId);
        });
    }

    @Test
    @DisplayName("장바구니 조회 성공")
    void getBaskets() {
        //given
        String basketId = "e623f3c2-4b79-4f3a-b876-9d1b5d47a283";
        String productId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Integer quantity = 2;
        Basket basket = Basket.builder()
                .quantity(quantity)
                .productId(UUID.fromString(productId))
                .basketId(UUID.fromString(basketId))
                .build();
        //when
        when(basketRepository.findAll()).thenReturn(List.of(basket));
        List<BasketGetResponseDto> response = basketService.getBaskets(1L);

        //then
        Assertions.assertEquals((List.of(BasketGetResponseDto.builder()
                .basketId(UUID.fromString(basketId))
                .productId(UUID.fromString(productId))
                .quantity(quantity)
                .build())), response);
    }

    @Test
    @DisplayName("장바구니 수정 성공")
    void updateBasket_success() {
        //given
        String basketId = "e623f3c2-4b79-4f3a-b876-9d1b5d47a283";
        String productId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Integer quantity = 2;
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(UUID.fromString(basketId))
                .quantity(quantity)
                .build();
        Basket basket = Basket.builder()
                .basketId(UUID.fromString(basketId))
                .productId(UUID.fromString(productId))
                .quantity(quantity)
                .build();

        given(basketRepository.findById(any())).willReturn(Optional.ofNullable(basket));
        given(basketRepository.save(any())).willReturn(basket);

        //when
        BasketResponseDto response = basketService.updateBasket(request);

        //then
        Assertions.assertEquals(UUID.fromString(basketId), response.getBasketId());
    }

    @Test
    @DisplayName("장바구니 수정 실패1 : 장바구니가 존재하지 않는 경우")
    void updateBasket_fail1() {
        //given
        String basketId = "e623f3c2-4b79-4f3a-b876-9d1b5d47a283";
        Integer quantity = 2;
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(UUID.fromString(basketId))
                .quantity(quantity)
                .build();

        //when & then
        when(basketRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            basketService.updateBasket(request);
        });
    }
}