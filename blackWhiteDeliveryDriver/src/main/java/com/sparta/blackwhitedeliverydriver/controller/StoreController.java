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
        List<StoreResponseDto> storeResponseDtoList = storeService.getStores();
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

    @PostMapping("/")
    public ResponseEntity<?> createStore(@Valid @RequestBody StoreRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // OWNER인지 확인 -> 본인 가게 등록
        if(!userDetails.getUser().getRole().equals(UserRoleEnum.OWNER)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Owner 권한이 있어야 점포 생성이 가능합니다.");
        }
        // 카테고리 등록
        Category category = categoryService.getOrCreateCategory(requestDto.getCategory(), userDetails.getUser());

        // 점포 등록
        UUID storeId = storeService.createStore(requestDto, category, userDetails.getUser());

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(storeId);

        return ResponseEntity.status(HttpStatus.CREATED).body(storeIdResponseDto);
    }
    // Manager, Master -> Owner의 가게 등록

    @PutMapping("/{storeId}")
    public ResponseEntity<?> updateStore(@PathVariable UUID storeId, @RequestBody StoreRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // OWNER의 가게인지 확인 -> 본인 가게만 수정
        if(!userDetails.getUser().getRole().equals(UserRoleEnum.OWNER) || !isStoreOfOwner(storeId, userDetails)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 점포만 수정이 가능합니다.");
        }

        // 점포 수정
        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(storeService.updateStore(storeId, requestDto, userDetails));

        return ResponseEntity.status(HttpStatus.CREATED).body(storeIdResponseDto);
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<?> deleteStore(@PathVariable UUID storeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // OWNER의 가게인지 확인 -> 본인 가게만 수정
        if(!userDetails.getUser().getRole().equals(UserRoleEnum.OWNER) || !isStoreOfOwner(storeId, userDetails)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 점포만 수정이 가능합니다.");
        }

        // 점포 삭제
        storeService.deleteStore(storeId, userDetails);

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(storeIdResponseDto);
    }

    private boolean isStoreOfOwner(UUID storeId, UserDetailsImpl userDetails) {
        String ownerNameOfStore = storeService.getNameOfOwner(storeId);
        if(ownerNameOfStore.matches(userDetails.getUser().getUsername())){ return true; }
        return false;
    }
}
