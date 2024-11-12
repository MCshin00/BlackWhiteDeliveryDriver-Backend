package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.service.BasketService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/baskets")
@RequiredArgsConstructor
@Slf4j
public class BasketController {

    private final BasketService basketService;

    @Secured({"ROLE_CUSTOMER"})
    @PostMapping
    public ResponseEntity<?> addProductToBasket(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @Valid @RequestBody BasketAddRequestDto request) {
        //장바구니 추가
        BasketResponseDto response = basketService.addProductToBasket(userDetails.getUsername(), request);

        //201 응답
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
