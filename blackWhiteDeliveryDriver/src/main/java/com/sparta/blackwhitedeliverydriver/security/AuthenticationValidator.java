package com.sparta.blackwhitedeliverydriver.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.RestApiException;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationValidator {
    private final UserRepository userRepository;

    @Autowired
    public AuthenticationValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateHttpMethod(HttpServletRequest request) {
        if (!request.getMethod().equals(HttpMethod.POST.name())) {
            throw new IllegalArgumentException(ExceptionMessage.NOT_ALLOWED_METHOD.getMessage());
        }
    }

    public void validateNotAlreadyLoggedIn() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            throw new IllegalStateException(ExceptionMessage.ALREADY_LOGGED_IN.getMessage());
        }
    }

    public void validateUser(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        if (user.getDeletedBy() != null && user.getDeletedDate() != null) {
            throw new IllegalStateException(ExceptionMessage.USER_DELETED.getMessage());
        }
    }

    public void jwtExceptionHandler(HttpServletResponse response, String errorMessage, int statusCode) {
        response.setStatus(statusCode);  // 상태 코드 설정
        response.setContentType("application/json");  // 응답 형식 설정
        response.setCharacterEncoding("UTF-8");  // 문자 인코딩 설정
        try {
            RestApiException exception = new RestApiException(errorMessage, statusCode);
            String json = new ObjectMapper().writeValueAsString(exception);

            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
