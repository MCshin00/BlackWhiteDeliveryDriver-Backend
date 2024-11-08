package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.BasketRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketResponseDto;
import org.springframework.stereotype.Service;

@Service
public class BasketService {
    public BasketResponseDto addProductToBasket(BasketRequestDto request) {
        return BasketResponseDto.builder().basketId("e623f3c2-4b79-4f3a-b876-9d1b5d47a283").build();
    }
}
