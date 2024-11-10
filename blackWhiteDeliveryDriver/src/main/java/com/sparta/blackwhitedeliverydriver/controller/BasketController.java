package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.BasketGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketRemoveRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.service.BasketService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/basket")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;
    @PostMapping
    public BasketResponseDto addProductToBasket(BasketAddRequestDto request){
        return basketService.addProductToBasket(request);
    }

    @DeleteMapping("/{basketId}")
    public BasketResponseDto removeProductFromBasket(@PathVariable String basketId){
        return basketService.removeProductFromBasket(basketId);
    }

    @GetMapping
    public List<BasketGetResponseDto> getBaskets(){
        return basketService.getBaskets(1L);
    }
}
