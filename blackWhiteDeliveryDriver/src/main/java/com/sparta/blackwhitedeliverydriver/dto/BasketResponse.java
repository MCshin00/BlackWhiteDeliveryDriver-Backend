package com.sparta.blackwhitedeliverydriver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BasketResponse {
    private Long userId;
    private String productId;
    private Integer quantity;
}
