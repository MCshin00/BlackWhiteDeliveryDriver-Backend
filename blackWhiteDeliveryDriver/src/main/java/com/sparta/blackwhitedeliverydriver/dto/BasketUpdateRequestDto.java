package com.sparta.blackwhitedeliverydriver.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BasketUpdateRequestDto {
    private UUID basketId;
    private Integer quantity;
}
