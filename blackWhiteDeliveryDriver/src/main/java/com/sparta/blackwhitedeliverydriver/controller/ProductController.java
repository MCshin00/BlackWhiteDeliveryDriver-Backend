package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.CreateProductRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.ProductIdResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.ProductResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.service.ProductService;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.service.StoreService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
public class ProductController {

    private final StoreService storeService;
    private final ProductService productService;

    @GetMapping("/")
    public ResponseEntity<?> getProducts(@RequestParam UUID storeId) {
        // 해당 가게의 모든 음식 조회
        List<ProductResponseDto> productResponseDtoList = productService.getProducts(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(productResponseDtoList);
    }

    @PostMapping("/")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequestDto requestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println("음식 등록 검증");
        if(userDetails.getUser().getRole().equals(UserRoleEnum.CUSTOMER)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("CUSTOMER는 음식을 등록할 권한이 없습니다.");
        }
        else if(userDetails.getUser().getRole().equals(UserRoleEnum.OWNER)){
            // 가게 주인이 자신의 가게에 등록
            String nameOfOwner = storeService.getNameOfOwner(requestDto.getStoreId());
            if(!nameOfOwner.equals(userDetails.getUser().getUsername())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("가게의 OWNER만 음식을 등록할 수 있습니다.");
            }
        }
        // Manager or Master가 등록

        // 음식 등록
        System.out.println("음식 등록");
        UUID productId = productService.createProduct(requestDto, userDetails.getUser());
        System.out.println("음식 ID 조회");
        ProductIdResponseDto productIdResponseDto = new ProductIdResponseDto(productId);
        System.out.println("음식 등록 끝");
        return ResponseEntity.status(HttpStatus.CREATED).body(productIdResponseDto);
    }
}
