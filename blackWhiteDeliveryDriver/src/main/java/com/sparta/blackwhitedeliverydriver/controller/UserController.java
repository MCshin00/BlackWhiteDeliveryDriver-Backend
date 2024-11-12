package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.SignupRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.UsernameResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.UpdateUserRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.UserResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        // 사용자 등록 처리
        UsernameResponseDto responseDto = userService.signup(requestDto, null);

        // 성공 응답으로 201 Created와 사용자 ID 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/signup/master")
    public ResponseEntity<?> signupManager(@Valid @RequestBody SignupRequestDto requestDto) {
        // 사용자 등록 처리
        UsernameResponseDto responseDto = userService.signup(requestDto, UserRoleEnum.MASTER);

        // 성공 응답으로 201 Created와 사용자 ID 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/")
    public ResponseEntity<?> getUserInfo(@RequestParam String username, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 사용자 정보 가져오기
        UserResponseDto responseDto = userService.getUserInfo(username, userDetails.getUser());

        // 성공 응답으로 200 OK와 사용자 ID 반환
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 사용자 정보 업데이트
        UsernameResponseDto responseDto = userService.updateUser(requestDto, userDetails.getUsername());

        // 성공 응답으로 200 OK와 사용자 ID 반환
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Secured({"ROLE_MANAGER", "ROLE_MASTER"})
    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequestDto requestDto, @PathVariable String username) {
        // 사용자 정보 업데이트
        UsernameResponseDto responseDto = userService.updateUser(requestDto, username);

        // 성공 응답으로 200 OK와 사용자 ID 반환
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 사용자 정보 삭제(soft-delete)
        UsernameResponseDto responseDto = userService.deleteUser(userDetails.getUsername());

        // 성공 응답으로 200 OK와 사용자 ID 반환
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Secured({"ROLE_MANAGER", "ROLE_MASTER"})
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        // 사용자 정보 삭제(soft-delete)
        UsernameResponseDto responseDto = userService.deleteUser(username);

        // 성공 응답으로 200 OK와 사용자 ID 반환
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
