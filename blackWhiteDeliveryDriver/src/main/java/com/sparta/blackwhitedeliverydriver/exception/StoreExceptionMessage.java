package com.sparta.blackwhitedeliverydriver.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreExceptionMessage {
    STORE_NOT_FOUND("해당 점포를 찾을 수 없습니다.");
    private final String message;
}
