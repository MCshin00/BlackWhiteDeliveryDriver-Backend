package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.AddressRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.AddressResponseDto;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.service.AddressService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/address")
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/")
    public ResponseEntity<?> createAddress(@Valid @RequestBody AddressRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 주소 등록 처리
        addressService.createAddress(requestDto, userDetails.getUser());

        // 성공 응답으로 201 Created와 메세지 반환
        return ResponseEntity.status(HttpStatus.CREATED).body("status : 201 CREATED");
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<?> updateAddress(@Valid @RequestBody AddressRequestDto requestDto,
                                           @PathVariable Long addressId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 주소 수정 처리
        addressService.updateAddress(requestDto, addressId, userDetails.getUser());

        // 성공 응답으로 200 OK와 메세지 반환
        return ResponseEntity.status(HttpStatus.OK).body("status : 200 OK");
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllAddress(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 사용자의 모든 주소를 가져온다
        List<AddressResponseDto> addressResponseDtos = addressService.getAllAddresses(userDetails.getUser());

        // 성공 응답으로 200 OK와 AddressResponseDto 리스트 반환
        return ResponseEntity.status(HttpStatus.OK).body(addressResponseDtos);
    }

    @PutMapping("/{addressId}/current")
    public ResponseEntity<?> setCurrentAddress(@PathVariable Long addressId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        addressService.setCurrentAddress(addressId, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body("status : 200 OK");
    }
}
