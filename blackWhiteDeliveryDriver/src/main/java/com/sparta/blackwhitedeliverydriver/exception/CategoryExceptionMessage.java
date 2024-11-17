package com.sparta.blackwhitedeliverydriver.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryExceptionMessage {
    CATEGORY_DUPLICATED("중복된 카테고리 이름이 있습니다.");
    private final String message;
}
