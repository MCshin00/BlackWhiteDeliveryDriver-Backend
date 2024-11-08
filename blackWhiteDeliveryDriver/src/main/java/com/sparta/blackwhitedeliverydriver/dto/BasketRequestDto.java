package com.sparta.blackwhitedeliverydriver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BasketRequestDto {
    private Long userId;
    private String productId;
    private Integer quantity;
}
