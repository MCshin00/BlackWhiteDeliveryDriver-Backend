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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditorAware<String> auditorAware;

    @Transactional
    public UsernameResponseDto signup(@Valid SignupRequestDto requestDto, UserRoleEnum loggedInRole) {
        checkUsername(requestDto.getUsername());
        checkEmail(requestDto.getEmail());
        checkPhoneNumber(requestDto.getPhoneNumber());
        if (loggedInRole != UserRoleEnum.MASTER) {
            checkRole(requestDto.getRole());
        }

        User user = User.from(requestDto, passwordEncoder);

        // 로그인된 사용자가 있을 경우 그 사용자의 username을 CreatedBy로 설정, 없는 경우 회원가입 시 지정한 username이 됨
        String createdBy = auditorAware.getCurrentAuditor()
                .orElse(requestDto.getUsername());
        user.setCreatedBy(createdBy);
        user.setLastModifiedBy(createdBy);
        User savedUser = userRepository.save(user);  // User 엔티티 저장

        return new UsernameResponseDto(savedUser.getUsername());  //저장된 User Entity의 id값을 통해 SignupResponseDto를 생성하고 반환
    }

    public UserResponseDto getUserInfo(String username, String loggedInUsername) {
        User loggedInUser = userRepository.findById(loggedInUsername)
                .orElseThrow(() -> new UsernameNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        // 요청한 사용자의 역할이 MANAGER나 MASTER가 아닌 경우 탈퇴한 사용자거나 프로필이 비공개 상태인지 확인
        // MANAGER나 MASTER인 경우에는 탈퇴한 사용자나 프로필이 비공개 상태인 사용자의 정보도 볼 수 있음
        if (loggedInUser.getRole() != UserRoleEnum.MANAGER && loggedInUser.getRole() != UserRoleEnum.MASTER) {
            checkDeletedUser(user);
            checkPublicProfile(user);
        }

        return UserResponseDto.from(user);
    }

    @Transactional
    public UsernameResponseDto updateUser(@Valid UpdateUserRequestDto requestDto, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        checkDeletedUser(user);

        user.update(requestDto, passwordEncoder);

        userRepository.save(user);

        return new UsernameResponseDto(user.getUsername());
    }

    @Transactional
    public UsernameResponseDto deleteUser(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        checkDeletedUser(user);

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

    private void checkDeletedUser(User user) {
        if (user.getDeletedDate() != null || user.getDeletedBy() != null) {
            throw new IllegalArgumentException(ExceptionMessage.USER_DELETED.getMessage());
        }
    }

    private void checkPublicProfile(User user) {
        if (!user.isPublicProfile()) {
            throw new IllegalArgumentException(ExceptionMessage.USER_NOT_PUBLIC.getMessage());
        }
    }
}
