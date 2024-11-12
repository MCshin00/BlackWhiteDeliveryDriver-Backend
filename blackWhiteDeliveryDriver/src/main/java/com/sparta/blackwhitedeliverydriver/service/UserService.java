package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.SignupRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.UpdateUserRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.UserResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.UsernameResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditorAware<String> auditorAware;

    public UsernameResponseDto signup(@Valid SignupRequestDto requestDto, UserRoleEnum loggedInRole) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String email = requestDto.getEmail();
        String phoneNumber = requestDto.getPhoneNumber();
        UserRoleEnum role = requestDto.getRole();

        checkUsername(username);
        checkEmail(email);
        checkPhoneNumber(phoneNumber);
        if (loggedInRole != UserRoleEnum.MASTER) {
            checkRole(role);
        }

        User user = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .phoneNumber(phoneNumber)
                .role(role)
                .build();

        // 로그인된 사용자가 있을 경우 그 사용자의 username을 CreatedBy로 설정, 없는 경우 회원가입 시 지정한 username이 됨
        String createdBy = getCurrentUsername(username);
        user.setCreatedBy(createdBy);
        user.setLastModifiedBy(createdBy);
        User savedUser = userRepository.save(user);  // User 엔티티 저장

        return new UsernameResponseDto(savedUser.getUsername());  //저장된 User Entity의 id값을 통해 SignupResponseDto를 생성하고 반환
    }

    public UserResponseDto getUserInfo(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        return new UserResponseDto(user);
    }

    @Transactional
    public UsernameResponseDto updateUser(@Valid UpdateUserRequestDto requestDto, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setEmail(requestDto.getEmail());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        user.setImageUrl(requestDto.getImgUrl());
        user.setPublicProfile(requestDto.isPublicProfile());
        user.setRole(requestDto.getRole());

        userRepository.save(user);

        return new UsernameResponseDto(user.getUsername());
    }

    @Transactional
    public UsernameResponseDto deleteUser(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        // 삭제를 수행한 사용자의 username을 가져옵니다.
        String deletedBy = auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.LOGIN_NOT_FOUND.getMessage()));

        user.setDeletedBy(deletedBy);
        user.setDeletedDate(LocalDateTime.now());

        userRepository.save(user);

        return new UsernameResponseDto(user.getUsername());
    }

    private void checkUsername(String username) {
        Optional<User> checkUsername = userRepository.findById(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException(ExceptionMessage.DUPLICATED_USERNAME.getMessage());
        }
    }

    private void checkEmail(String email) {
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException(ExceptionMessage.DUPLICATED_EMAIL.getMessage());
        }
    }

    private void checkPhoneNumber(String phoneNumber) {
        Optional<User> checkPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);
        if (checkPhoneNumber.isPresent()) {
            throw new IllegalArgumentException(ExceptionMessage.DUPLICATED_PHONENUMBER.getMessage());
        }
    }

    private void checkRole(UserRoleEnum role) {
        if (role != UserRoleEnum.CUSTOMER && role != UserRoleEnum.OWNER) {
            throw new IllegalArgumentException(ExceptionMessage.NOT_ALLOEWD_ROLE.getMessage());
        }
    }

    // 현재 로그인된 사용자의 username을 가져오는 메서드
    private String getCurrentUsername(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증되지 않은 경우, 즉 anonymousUser일 경우
        if (authentication instanceof AnonymousAuthenticationToken) {
            return username;  // 로그인되지 않은 경우, 요청된 username을 return
        }
        // 로그인된 경우, 사용자 이름을 반환
        return authentication.getName();
    }
}
