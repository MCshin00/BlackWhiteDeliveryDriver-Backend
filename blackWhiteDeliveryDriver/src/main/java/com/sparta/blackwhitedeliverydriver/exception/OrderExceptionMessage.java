package com.sparta.blackwhitedeliverydriver.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderExceptionMessage {
    ORDER_NOT_FOUND("주문 내역을 찾을 수 없습니다."),
    ORDER_USER_NOT_EQUALS("주문서의 유저가 아닙니다.") ;
    private final String message;
}
