package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.BasketGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.entity.Basket;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.repository.BasketRepository;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final UserRepository userRepository;

    public BasketResponseDto addProductToBasket(String username, BasketAddRequestDto request) {
        // 유저가 유효성 체크
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        // 상품이 유효성, 중복 체크
        // 같은 지점에서 담은 상품인지 체크

        Basket basket = basketRepository.save(Basket.from(user, request));
        return BasketResponseDto.from(basket);
    }

    public BasketResponseDto removeProductFromBasket(String basketId) {
        UUID basketUUID = UUID.fromString(basketId);
        //유저 유효성 검사
        //장바구니 유효성 검사
        Basket basket = basketRepository.findById(basketUUID).orElseThrow(() ->
                new IllegalArgumentException("장바구니가 존재하지 않습니다."));

        basketRepository.delete(basket);
        return BasketResponseDto.from(basket);
    }

    public List<BasketGetResponseDto> getBaskets(Long userId) {
        // 유저 유효성 검증
        // 로그인 연동시 해당 유저의 장바구니를 조회하도록 수정 필요
        List<Basket> basketList = basketRepository.findAll();
        return basketList.stream().map(BasketGetResponseDto::from).collect(Collectors.toList());
    }

    @Transactional
    public BasketResponseDto updateBasket(BasketUpdateRequestDto request) {
        //유저 유효성 검사
        //장바구니 유효성 검사
        Basket basket = basketRepository.findById(request.getBasketId()).orElseThrow(() ->
                new IllegalArgumentException("장바구니가 존재하지 않습니다."));

        basket.updateBasketOfQuantity(request.getQuantity());
        basketRepository.save(basket);

        return BasketResponseDto.from(basket);
    }

    private boolean isValidQuantity(int quantity){
        return quantity >= 0 && quantity < 100;
    }
}
