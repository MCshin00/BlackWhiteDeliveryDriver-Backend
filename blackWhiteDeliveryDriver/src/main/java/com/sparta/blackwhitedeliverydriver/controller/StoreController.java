package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.StoreRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreResponseDto;
import com.sparta.blackwhitedeliverydriver.service.StoreService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/")
    public ResponseEntity<?> getStores(){
        // OWNER인지 확인

        // 점포 목록
        List<StoreResponseDto> storeResponseDtoList = storeService.getStores();
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDtoList);
    }

    @PostMapping("/")
    public ResponseEntity<?> createStore(@Valid @RequestBody StoreRequestDto requestDto) {
        // OWNER인지 확인

        // 점포 등록
        UUID storeId = storeService.createStore(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(storeId);
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<?> updateStore(@PathVariable UUID storeId, @RequestBody StoreRequestDto requestDto) {
        // OWNER인지 확인

        // 점포 수정
        storeService.updateStore(storeId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(storeId);
    }


}
