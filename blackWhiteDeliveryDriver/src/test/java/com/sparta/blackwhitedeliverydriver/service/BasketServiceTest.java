package com.sparta.blackwhitedeliverydriver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.sparta.blackwhitedeliverydriver.entity.Product;
import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.exception.BasketExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.repository.BasketRepository;
import com.sparta.blackwhitedeliverydriver.repository.OrderRepository;
import com.sparta.blackwhitedeliverydriver.repository.ProductRepository;
import com.sparta.blackwhitedeliverydriver.repository.StoreRepository;
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
    StoreRepository storeRepository = mock(StoreRepository.class);
    ProductRepository productRepository = mock(ProductRepository.class);
    OrderRepository orderRepository = mock(OrderRepository.class);

    @BeforeEach
    public void setUp() {
        basketService = new BasketService(basketRepository, userRepository, storeRepository, productRepository,orderRepository);
    }

    @Test
    @DisplayName("장바구니 담기 성공")
    void addProductToBasket_success() {
        //given
        String username = "user";
        String storeName = "store";
        UUID basketId = UUID.randomUUID();
        UUID basketId2 = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        int quantity = 2;
        Store store = Store.builder()
                .storeId(storeId)
                .storeName(storeName)
                .build();
        Product product = Product.builder()
                .productId(productId)
                .store(store)
                .build();
        Product product2 = Product.builder()
                .productId(productId2)
                .store(store)
                .build();
        Basket basket = Basket.builder()
                .id(basketId)
                .product(product)
                .build();
        Basket basket2 = Basket.builder()
                .id(basketId2)
                .product(product2)
                .build();
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username(username)
                .build();
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(productId2)
                .quantity(quantity)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(storeRepository.findById(any())).willReturn(Optional.ofNullable(store));
        given(productRepository.findById(any())).willReturn(Optional.ofNullable(product2));
        when(basketRepository.findAllByUser(any())).thenReturn(List.of(basket));
        when(basketRepository.save(any())).thenReturn(basket2);

        //when
        BasketResponseDto response = basketService.addProductToBasket(username, request);

        //then
        Assertions.assertEquals(basketId2, response.getBasketId());
    }

    @Test
    @DisplayName("장바구니 담기 실패1 : 유저가 유효하지 않는 경우")
    void addProductToBasket_fail1() {
        //given
        String username = "user1";
        UUID productId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        int quantity = 2;
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(productId)
                .quantity(quantity)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> basketService.addProductToBasket(username, request));
        assertEquals(ExceptionMessage.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 담기 실패2 : 점포가 없는 경우")
    void addProductToBasket_fail2() {
        //given
        String username = "user1";
        UUID productId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        int quantity = 2;
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username("user1")
                .build();
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(productId)
                .quantity(quantity)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        when(storeRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> basketService.addProductToBasket(username, request));
        Assertions.assertEquals("점포를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 담기 실패3 : 상품이 존재하지 않는 경우")
    void addProductToBasketToBasket_fail3() {
        //given
        String username = "user";
        String storeName = "store";
        UUID productId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        int quantity = 2;
        Store store = Store.builder()
                .storeId(storeId)
                .storeName(storeName)
                .build();
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username(username)
                .build();
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(productId)
                .quantity(quantity)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(storeRepository.findById(any())).willReturn(Optional.ofNullable(store));
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> basketService.addProductToBasket(username, request));
        assertEquals(exception.getMessage(), "상품을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("장바구니 담기 실패4 : 상품이 증복된 경우")
    void addProductToBasketToBasket_fail4() {
        //given
        String username = "user";
        String storeName = "store";
        UUID productId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        int quantity = 2;
        Store store = Store.builder()
                .storeId(storeId)
                .storeName(storeName)
                .build();
        Product product = Product.builder()
                .productId(productId)
                .build();
        Basket basket = Basket.builder()
                .product(product)
                .build();
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username(username)
                .build();
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(productId)
                .quantity(quantity)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(storeRepository.findById(any())).willReturn(Optional.ofNullable(store));
        given(productRepository.findById(any())).willReturn(Optional.ofNullable(product));
        when(basketRepository.findAllByUser(any())).thenReturn(List.of(basket));

        //when & then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> basketService.addProductToBasket(username, request));
        assertEquals(BasketExceptionMessage.BASKET_DUPLICATED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 담기 실패5 : 점포이 다른 경우")
    void addProductToBasketToBasket_fail5() {
        //given
        String username = "user";
        String storeName = "store";
        UUID productId = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        UUID storeId2 = UUID.randomUUID();
        int quantity = 2;
        Store store = Store.builder()
                .storeId(storeId)
                .storeName(storeName)
                .build();
        Store store2 = Store.builder()
                .storeId(storeId2)
                .build();
        Product product = Product.builder()
                .productId(productId)
                .store(store)
                .build();
        Product product2 = Product.builder()
                .productId(productId2)
                .store(store2)
                .build();
        Basket basket = Basket.builder()
                .product(product)
                .build();
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username(username)
                .build();
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(product2.getProductId())
                .quantity(quantity)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(storeRepository.findById(any())).willReturn(Optional.ofNullable(store));
        given(productRepository.findById(any())).willReturn(Optional.of(product2));
        when(basketRepository.findAllByUser(any())).thenReturn(List.of(basket));

        //when & then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> basketService.addProductToBasket(username, request));
        assertEquals(BasketExceptionMessage.BASKET_DIFFERENT_STORE.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 빼기 성공")
    void removeProductFromBasket_success() {
        //given
        UUID basketId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        String username = "user";
        String storeName = "store";
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username(username)
                .build();
        Store store = Store.builder()
                .storeId(storeId)
                .storeName(storeName)
                .build();
        Basket basket = Basket.builder()
                .id(basketId)
                .store(store)
                .quantity(2)
                .user(user)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(basketRepository.findById(any())).willReturn(Optional.ofNullable(basket));
        doNothing().when(basketRepository).delete(any()); //repository delete 건너뛰기

        //when
        BasketResponseDto response = basketService.removeProductFromBasket(username, basketId);

        //then
        Assertions.assertEquals(basketId, response.getBasketId());
    }


    @Test
    @DisplayName("장바구니 빼기 실패1 : 장바구니 존재하지 않는 경우")
    void removeProductFromBasket_fail1() {
        //given
        UUID basketId = UUID.randomUUID();
        String username = "user";
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username(username)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));

        when(basketRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> basketService.removeProductFromBasket(username, basketId));
        assertEquals(BasketExceptionMessage.BASKET_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 빼기 실패2 : 유저가 유효하지 않는 경우")
    void removeProductFromBasket_fail2() {
        //given
        UUID basketId = UUID.randomUUID();
        String username = "user";

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> basketService.removeProductFromBasket(username, basketId));
        assertEquals(ExceptionMessage.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 빼기 실패3 : 장바구니 유저와 api 호출 유저가 일치하지 않는 경우")
    void removeProductFromBasket_fail3() {
        //given
        UUID basketId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        String username1 = "user1";
        String username2 = "user2";
        String storeName = "store";
        User user1 = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username(username1)
                .build();
        User user2 = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username(username2)
                .build();
        Store store = Store.builder()
                .storeId(storeId)
                .storeName(storeName)
                .build();
        Basket basket = Basket.builder()
                .id(basketId)
                .store(store)
                .quantity(2)
                .user(user1)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user2));
        given(basketRepository.findById(any())).willReturn(Optional.ofNullable(basket));

        //when & then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> basketService.removeProductFromBasket(username2, basketId));
        assertEquals(BasketExceptionMessage.BASKET_USER_NOT_EQUALS.getMessage(), exception.getMessage());
    }

    /*
    @Test
    @DisplayName("장바구니 조회 성공")
    void getBaskets() {
        //given
        String username = "user";
        String storeName = "store";
        String productName = "product";
        Integer quantity = 2;
        UUID basketId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username(username)
                .build();
        Store store = Store.builder()
                .storeId(storeId)
                .storeName(storeName)
                .build();
        Product product = Product.builder()
                .productId(productId)
                .name(productName)
                .build();
        Basket basket = Basket.builder()
                .id(basketId)
                .store(store)
                .product(product)
                .quantity(quantity)
                .user(user)
                .build();
        BasketGetResponseDto responseDto = BasketGetResponseDto.builder()
                .basketId(basketId)
                .username(username)
                .storeId(storeId)
                .productId(productId)
                .quantity(quantity)
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(basketRepository.findAllByUser(any())).thenReturn(List.of(basket));

        //when
        List<BasketGetResponseDto> response = basketService.getBaskets(username);

        //then
        Assertions.assertEquals(responseDto.getBasketId(), response.get(0).getBasketId());
    }

    @Test
    @DisplayName("장바구니 조회 실패 : 유저가 유효하지 않는 경우")
    void getBaskets_fail1() {
        //given
        String username = "user1";
        given(userRepository.findById(any())).willReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(NullPointerException.class, () -> basketService.getBaskets(username));
        assertEquals(ExceptionMessage.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

     */

    @Test
    @DisplayName("장바구니 수정 성공")
    void updateBasket_success() {
        //given
        String username = "user1";
        String storeName = "storeName";
        UUID basketId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        Integer quantity = 3;
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username(username)
                .build();
        Store store = Store.builder()
                .storeId(storeId)
                .storeName(storeName)
                .build();
        Basket basket = Basket.builder()
                .id(basketId)
                .store(store)
                .quantity(2)
                .user(user)
                .build();
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(basketId)
                .quantity(quantity)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(basketRepository.findById(any())).willReturn(Optional.ofNullable(basket));
        given(basketRepository.save(any())).willReturn(basket);

        //when
        BasketResponseDto response = basketService.updateBasket(username, request);

        //then
        Assertions.assertEquals(basketId, response.getBasketId());
        assert basket != null;
        assertEquals(quantity, basket.getQuantity());
    }

    @Test
    @DisplayName("장바구니 수정 실패1 : 장바구니가 존재하지 않는 경우")
    void updateBasket_fail1() {
        //given
        UUID basketId = UUID.randomUUID();
        String username = "user1";
        Integer quantity = 2;
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(basketId)
                .quantity(quantity)
                .build();
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username(username)
                .build();
        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        when(basketRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> basketService.updateBasket("user1", request));
        assertEquals(BasketExceptionMessage.BASKET_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 수정 실패2 : 유저가 존재하지 않는 경우")
    void updateBasket_fail2() {
        //given
        UUID basketId = UUID.randomUUID();
        Integer quantity = 2;
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(basketId)
                .quantity(quantity)
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> basketService.updateBasket("user1", request));
        assertEquals(ExceptionMessage.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 수정 실패3 : 장바구니 유저와 api 호출한 유저가 일치하지 않는 경우")
    void updateBasket_fail3() {
        //given
        UUID basketId = UUID.randomUUID();
        Integer quantity = 2;
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(basketId)
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
                .id(basketId)
                .quantity(quantity)
                .user(user)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user2));
        given(basketRepository.findById(any())).willReturn(Optional.ofNullable(basket));
        given(basketRepository.save(any())).willReturn(basket);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> basketService.updateBasket("user2", request));
    }
}