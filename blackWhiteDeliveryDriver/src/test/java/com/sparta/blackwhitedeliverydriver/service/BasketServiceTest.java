package com.sparta.blackwhitedeliverydriver.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sparta.blackwhitedeliverydriver.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.entity.Basket;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.repository.BasketRepository;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
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
    UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    public void setUp() {
        basketService = new BasketService(basketRepository, userRepository);
    }

    @Test
    @DisplayName("장바구니 담기 성공")
    void addProductToBasket_success() {
        //given
        String username = "user1";
        String productId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        String basketId = "7c9f6b72-4d8e-49b0-9b6e-7fc8f0e905d9";
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(UUID.fromString(productId))
                .quantity(2)
                .build();
        Basket basket = Basket.builder()
                .id(UUID.fromString(basketId))
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .build();
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username("user1")
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));

        given(basketRepository.save(any())).willReturn(basket);

        //when
        BasketResponseDto response = basketService.addProductToBasket(username, request);

        //then
        Assertions.assertEquals(UUID.fromString(basketId), response.getBasketId());
    }

    @Test
    @DisplayName("장바구니 담기 실패 : 유저가 유효하지 않는 경우")
    void addProductToBasket_fail1() {
        //given
        String username = "user1";
        String productId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(UUID.fromString(productId))
                .quantity(2)
                .build();

        //when
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        assertThrows(NullPointerException.class, () -> {
            basketService.addProductToBasket(username, request);
        });
    }

    @Test
    @DisplayName("장바구니 빼기 성공")
    void removeProductFromBasket_success() {
        //given
        String basketId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        String productId = "1e217cbf-e3ec-4e85-9f9b-42557c1dd079";
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username("user1")
                .build();
        Basket basket = Basket.builder()
                .id(UUID.fromString(basketId))
                .productId(UUID.fromString(productId))
                .quantity(2)
                .user(user)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(basketRepository.findById(any())).willReturn(Optional.ofNullable(basket));

        //when
        doNothing().when(basketRepository).delete(any()); //repository delete 건너뛰기
        BasketResponseDto response = basketService.removeProductFromBasket(user.getUsername(), basketId);

        //then
        Assertions.assertEquals(UUID.fromString(basketId), response.getBasketId());
    }


    @Test
    @DisplayName("장바구니 빼기 실패1 : 장바구니 존재하지 않는 경우")
    void removeProductFromBasket_fail1() {
        //given
        String username = "user1";
        String basketId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username("user1")
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));

        //when
        when(basketRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        assertThrows(NullPointerException.class, () -> {
            basketService.removeProductFromBasket(username, basketId);
        });
    }

    @Test
    @DisplayName("장바구니 빼기 실패2 : 유저가 유효하지 않는 경우")
    void removeProductFromBasket_fail2() {
        //given
        String username = "user1";
        String basketId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";

        //when
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        assertThrows(NullPointerException.class, () -> {
            basketService.removeProductFromBasket(username, basketId);
        });
    }

    @Test
    @DisplayName("장바구니 빼기 실패3 : 장바구니 유저와 api 호출 유저가 일치하지 않는 경우")
    void removeProductFromBasket_fail3() {
        //given
        String basketId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        String productId = "1e217cbf-e3ec-4e85-9f9b-42557c1dd079";
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username("user1")
                .build();
        User user2 = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username("user2")
                .build();
        Basket basket = Basket.builder()
                .id(UUID.fromString(basketId))
                .productId(UUID.fromString(productId))
                .quantity(2)
                .user(user)
                .build();
        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user2));
        given(basketRepository.findById(any())).willReturn(Optional.ofNullable(basket));

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            basketService.removeProductFromBasket(user2.getUsername(), basketId);
        });
    }

    @Test
    @DisplayName("장바구니 조회 성공")
    void getBaskets() {
        //given
        String username = "user1";
        String basketId = "e623f3c2-4b79-4f3a-b876-9d1b5d47a283";
        String productId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Integer quantity = 2;
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username(username)
                .build();
        Basket basket = Basket.builder()
                .quantity(quantity)
                .productId(UUID.fromString(productId))
                .user(user)
                .id(UUID.fromString(basketId))
                .build();
        //when
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(basketRepository.findAllByUser(any())).thenReturn(List.of(basket));
        List<BasketGetResponseDto> response = basketService.getBaskets(username);

        //then
        Assertions.assertEquals((List.of(BasketGetResponseDto.builder()
                .basketId(UUID.fromString(basketId))
                .username(username)
                .productId(UUID.fromString(productId))
                .quantity(quantity)
                .build())), response);
    }

    @Test
    @DisplayName("장바구니 조회 실패 : 유저가 유효하지 않는 경우")
    void getBaskets_fail1() {
        //given
        String username = "user1";
        given(userRepository.findById(any())).willReturn(Optional.empty());

        //when & then
        assertThrows(NullPointerException.class, () -> {
            basketService.getBaskets(username);
        });
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
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username("user1")
                .build();
        Basket basket = Basket.builder()
                .id(UUID.fromString(basketId))
                .productId(UUID.fromString(productId))
                .quantity(quantity)
                .user(user)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(basketRepository.findById(any())).willReturn(Optional.ofNullable(basket));
        given(basketRepository.save(any())).willReturn(basket);

        //when
        BasketResponseDto response = basketService.updateBasket(user.getUsername(), request);

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
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username("user1")
                .build();
        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        //when & then
        when(basketRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NullPointerException.class, () -> {
            basketService.updateBasket("user1", request);
        });
    }

    @Test
    @DisplayName("장바구니 수정 실패2 : 유저가 존재하지 않는 경우")
    void updateBasket_fail2() {
        //given
        String basketId = "e623f3c2-4b79-4f3a-b876-9d1b5d47a283";
        Integer quantity = 2;
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(UUID.fromString(basketId))
                .quantity(quantity)
                .build();
        //when & then
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NullPointerException.class, () -> {
            basketService.updateBasket("user1", request);
        });
    }

    @Test
    @DisplayName("장바구니 수정 실패3 : 장바구니 유저와 api 호출한 유저가 일치하지 않는 경우")
    void updateBasket_fail3() {
        //given
        String basketId = "e623f3c2-4b79-4f3a-b876-9d1b5d47a283";
        String productId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Integer quantity = 2;
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(UUID.fromString(basketId))
                .quantity(quantity)
                .build();
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username("user1")
                .build();
        User user2 = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username("user2")
                .build();
        Basket basket = Basket.builder()
                .id(UUID.fromString(basketId))
                .productId(UUID.fromString(productId))
                .quantity(quantity)
                .user(user)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user2));
        given(basketRepository.findById(any())).willReturn(Optional.ofNullable(basket));
        given(basketRepository.save(any())).willReturn(basket);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            basketService.updateBasket("user2", request);
        });
    }
}