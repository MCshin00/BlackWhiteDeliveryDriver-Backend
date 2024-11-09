package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.SignupRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.SignupResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.UpdateUserRequestDto;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditorAware<String> auditorAware;

    public SignupResponseDto signup(@Valid SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String email = requestDto.getEmail();
        String phoneNumber = requestDto.getPhoneNumber();
        UserRoleEnum role = requestDto.getRole();

        checkUsername(username);
        checkEmail(email);
        checkPhoneNumber(phoneNumber);

        User user = new User(username, password, email, phoneNumber, role);
        user.setCreatedBy(username);
        User savedUser = userRepository.save(user);  // User 엔티티 저장

        return new SignupResponseDto(savedUser.getId());  //저장된 User Entity의 id값을 통해 SignupResponseDto를 생성하고 반환
    }

    @Transactional
    public SignupResponseDto updateUser(@Valid UpdateUserRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setEmail(requestDto.getEmail());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        user.setImageUrl(requestDto.getImgUrl());
        user.setPublicProfile(requestDto.isPublicProfile());
        user.setRole(requestDto.getRole());

        userRepository.save(user);

        return new SignupResponseDto(user.getId());
    }

    @Transactional
    public SignupResponseDto deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 삭제를 수행한 사용자의 username을 가져옵니다.
        String deletedBy = auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new IllegalStateException("로그인 정보 없음"));

        user.setDeletedBy(deletedBy);
        user.setDeletedDate(LocalDateTime.now());

        userRepository.save(user);

        return new SignupResponseDto(user.getId());
    }

    private void checkUsername(String username) {
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
    }

    private void checkEmail(String email) {
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 이메일이 존재합니다.");
        }
    }

    private void checkPhoneNumber(String phoneNumber) {
        Optional<User> checkPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);
        if (checkPhoneNumber.isPresent()) {
            throw new IllegalArgumentException("중복된 전화번호가 존재합니다.");
        }
    }
}
