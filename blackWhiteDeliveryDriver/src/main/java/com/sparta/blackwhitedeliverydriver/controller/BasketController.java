package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.service.BasketService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/basket")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    @PostMapping
    public BasketResponseDto addProductToBasket(@RequestBody BasketAddRequestDto request) {
        return basketService.addProductToBasket(request);
    }

    @DeleteMapping("/{basketId}")
    public BasketResponseDto removeProductFromBasket(@PathVariable String basketId) {
        return basketService.removeProductFromBasket(basketId);
    }

    @GetMapping
    public List<BasketGetResponseDto> getBaskets() {
        return basketService.getBaskets(1L);
    }

    @PutMapping
    public BasketResponseDto updateBasket(@RequestBody BasketUpdateRequestDto request) {
        return basketService.updateBasket(request);
    }
}
