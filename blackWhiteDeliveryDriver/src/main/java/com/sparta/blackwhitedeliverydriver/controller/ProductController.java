package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.ProductResponseDto;
import com.sparta.blackwhitedeliverydriver.service.ProductService;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/store/{storeId}/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/")
    public ResponseEntity<?> getProducts(@PathVariable("storeId") UUID storeId) {
        // 해당 가게의 모든 음식 조회
        List<ProductResponseDto> productResponseDtoList = productService.getProducts(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(productResponseDtoList);
    }
}
