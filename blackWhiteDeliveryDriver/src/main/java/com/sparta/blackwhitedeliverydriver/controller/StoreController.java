package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.StoreIdResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Category;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.service.CategoryService;
import com.sparta.blackwhitedeliverydriver.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/stores")
public class StoreController {

    private final StoreService storeService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<?> getStores(){
        // 전체 점포 목록 조회
        List<StoreResponseDto> storeResponseDtoList = storeService.getStoreList();
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDtoList);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStoreById(@PathVariable("storeId") UUID storeId){
        StoreResponseDto storeResponseDto = storeService.getStore(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDto);
    }

    @GetMapping("/owner")
    public ResponseEntity<?> getStoreOfOwner(@AuthenticationPrincipal UserDetailsImpl userDetails){
        // OWNER가 등록한 가게 조회
        List<StoreResponseDto> storeResponseDtoList = storeService.getStoreOfOwner(userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDtoList);
    }

    @Secured("ROLE_OWNER")
    @PostMapping("/")
    public ResponseEntity<?> createStore(@Valid @RequestBody StoreRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 카테고리 등록
        List<Category> categoryList = categoryService.getOrCreateCategory(requestDto.getCategory(), userDetails.getUser());

        // 점포 등록
        UUID storeId = storeService.createStore(requestDto, categoryList, userDetails.getUser());

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(storeId);

        return ResponseEntity.status(HttpStatus.CREATED).body(storeIdResponseDto);
    }
    // Manager, Master -> Owner의 가게 등록

    @Secured("ROLE_OWNER, ROLE_MANAGER, ROLE_MASTER")
    @PutMapping("/{storeId}")
    public ResponseEntity<?> updateStore(@PathVariable UUID storeId, @RequestBody StoreRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 카테고리 등록
        List<Category> newCategoryList = categoryService.getOrCreateCategory(requestDto.getCategory(), userDetails.getUser());
        // 점포 수정
        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(storeService.updateStore(storeId, requestDto, newCategoryList, userDetails));

        return ResponseEntity.status(HttpStatus.OK).body(storeIdResponseDto);
    }

    @Secured("ROLE_MANAGER, ROLE_MASTER")
    @PutMapping("/{storeId}/public")
    public ResponseEntity<StoreIdResponseDto> updateStorePublic(@PathVariable UUID storeId, @RequestParam boolean isPublic) {
        StoreIdResponseDto storeIdResponseDto = storeService.updateStorePublic(storeId, isPublic);

        return ResponseEntity.status(HttpStatus.OK).body(storeIdResponseDto);
    }

    @Secured("ROLE_OWNER, ROLE_MANAGER, ROLE_MASTER")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<?> deleteStore(@PathVariable UUID storeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 점포 삭제
        storeService.deleteStore(storeId, userDetails);

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(storeIdResponseDto);
    }
}
