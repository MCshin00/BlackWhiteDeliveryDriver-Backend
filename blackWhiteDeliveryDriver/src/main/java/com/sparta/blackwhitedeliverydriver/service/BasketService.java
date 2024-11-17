package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.entity.Basket;
import com.sparta.blackwhitedeliverydriver.entity.Product;
import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.exception.BasketExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.StoreExceptionMessage;
import com.sparta.blackwhitedeliverydriver.repository.BasketRepository;
import com.sparta.blackwhitedeliverydriver.repository.ProductRepository;
import com.sparta.blackwhitedeliverydriver.repository.StoreRepository;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasketService {

    private final BasketRepository basketRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    @Transactional
    public BasketResponseDto addProductToBasket(String username, BasketAddRequestDto request) {
        // 유저가 유효성 체크
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        checkDeletedUser(user);

        // 같은 지점에서 담은 상품인지 체크
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new NullPointerException("점포를 찾을 수 없습니다."));
        checkDeletedStore(store);

        // 상품이 유효성, 중복 체크
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NullPointerException("상품을 찾을 수 없습니다."));
        checkDeletedProduct(product);

        // 담은 상품이 중복된 상품인지 체크
        List<Basket> baskets = basketRepository.findAllByUser(user);
        checkDuplicatedProduct(product, baskets);

        // 담은 상품의 지점이 장바구니 상품의 지점과 다른지 체크
        checkProductStore(product, baskets);

        Basket basket = Basket.ofUserAndStoreAndRequest(user, store, product, request);

        basket = basketRepository.save(basket);

        return BasketResponseDto.fromBasket(basket);
    }


    @Transactional
    public BasketResponseDto removeProductFromBasket(String username, UUID basketId) {
        //유저 유효성 검사
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        checkDeletedUser(user);

        //장바구니 유효성 검사
        Basket basket = basketRepository.findById(basketId).orElseThrow(() ->
                new NullPointerException(BasketExceptionMessage.BASKET_NOT_FOUND.getMessage()));
        checkDeletedBasket(basket);

        //유저와 장바구니 유저 체크
        checkBasketUser(user, basket);

        basket.updateDeleteInfo(username, LocalDateTime.now());
        basketRepository.save(basket);

        return BasketResponseDto.fromBasket(basket);
    }

    public Page<BasketGetResponseDto> getBaskets(String username, int page, int size, String sortBy, boolean isAsc) {
        // 유저 유효성 검증
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        checkDeletedUser(user);

        //페이징
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        //쿼리
        Page<Basket> baskets = basketRepository.findAllByUserAndNotDeleted(user, pageable);

        return baskets.map(BasketGetResponseDto::fromBasket);
    }

    @Transactional
    public BasketResponseDto updateBasket(String username, BasketUpdateRequestDto request) {
        //유저 유효성 검사
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        checkDeletedUser(user);

        //장바구니 유효성 검사
        Basket basket = basketRepository.findById(request.getBasketId()).orElseThrow(() ->
                new NullPointerException(BasketExceptionMessage.BASKET_NOT_FOUND.getMessage()));
        checkDeletedBasket(basket);

        //장바구니 유저와 api 호출 유저 체크
        checkBasketUser(user, basket);

        basket.updateBasketOfQuantity(request.getQuantity());
        basketRepository.save(basket);

        return BasketResponseDto.fromBasket(basket);
    }

    public Page<BasketGetResponseDto> searchBasketsByProductName(String productName, int page, int size, String sortBy, boolean isAsc) {
        // 페이징과 정렬 정보 생성
        //페이징
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 데이터 조회 및 변환
        Page<Basket> baskets = basketRepository.findByProductNameContainingAndNotDeleted(productName, pageable);
        return baskets.map(BasketGetResponseDto::fromBasket);
    }

    private void checkBasketUser(User user, Basket basket) {
        if (!user.getUsername().equals(basket.getUser().getUsername())) {
            throw new IllegalArgumentException(BasketExceptionMessage.BASKET_USER_NOT_EQUALS.getMessage());
        }
    }

    private void checkDuplicatedProduct(Product product, List<Basket> baskets) {
        for (Basket basket : baskets) {
            UUID productId = basket.getProduct().getProductId();
            if (productId.equals(product.getProductId())) {
                throw new IllegalArgumentException(BasketExceptionMessage.BASKET_DUPLICATED.getMessage());
            }
        }
    }

    private void checkProductStore(Product product, List<Basket> baskets) {
        for (Basket basket : baskets) {
            UUID storeId = basket.getProduct().getStore().getStoreId();
            if (!storeId.equals(product.getStore().getStoreId())) {
                throw new IllegalArgumentException(BasketExceptionMessage.BASKET_DIFFERENT_STORE.getMessage());
            }
        }
    }

    private void checkDeletedUser(User user) {
        if (user.getDeletedDate() != null || user.getDeletedBy() != null) {
            throw new IllegalArgumentException(ExceptionMessage.USER_DELETED.getMessage());
        }
    }

    private void checkDeletedStore(Store store) {
        if (store.getDeletedDate() != null || store.getDeletedBy() != null) {
            throw new IllegalArgumentException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage());
        }
    }

    private void checkDeletedProduct(Product product) {
        if (product.getDeletedDate() != null || product.getDeletedBy() != null) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
        }
    }

    private void checkDeletedBasket(Basket basket) {
        if (basket.getDeletedDate() != null || basket.getDeletedBy() != null) {
            throw new IllegalArgumentException(BasketExceptionMessage.BASKET_NOT_FOUND.getMessage());
        }
    }
}
