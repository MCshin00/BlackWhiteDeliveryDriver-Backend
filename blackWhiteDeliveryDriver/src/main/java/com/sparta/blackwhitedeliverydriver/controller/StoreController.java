package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.StoreIdResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreResponseDto;
import com.sparta.blackwhitedeliverydriver.service.StoreService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/stores")
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/")
    public ResponseEntity<?> getStores(){
        // OWNER인지 확인

        // 점포 목록
        List<StoreResponseDto> storeResponseDtoList = storeService.getStores();
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDtoList);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStoreById(@PathVariable("storeId") UUID storeId){
        StoreResponseDto storeResponseDto = storeService.getStore(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDto);
    }

    @PostMapping("/")
    public ResponseEntity<?> createStore(@Valid @RequestBody StoreRequestDto requestDto) {
        // OWNER인지 확인

        // 점포 등록
        UUID storeId = storeService.createStore(requestDto);

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(storeId);

        return ResponseEntity.status(HttpStatus.CREATED).body(storeIdResponseDto);
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<?> updateStore(@PathVariable UUID storeId, @RequestBody StoreRequestDto requestDto) {
        // OWNER인지 확인

        // 점포 수정
        storeService.updateStore(storeId, requestDto);

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(storeId);

        return ResponseEntity.status(HttpStatus.CREATED).body(storeIdResponseDto);
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<?> deleteStore(@PathVariable UUID storeId) {
        // 해당 가게 주인(OWNER)인지 확인

        // 점포 삭제
        storeService.deleteStore(storeId);

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(storeIdResponseDto);
    }
}
