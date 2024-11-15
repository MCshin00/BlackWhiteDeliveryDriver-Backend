package com.sparta.blackwhitedeliverydriver.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayExceptionMessage {
    PAY_NOT_FOUND("주문 내역을 찾을 수 없습니다.");
    private final String message;
}

