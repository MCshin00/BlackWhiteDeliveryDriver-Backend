package com.sparta.blackwhitedeliverydriver.dto;

import com.sparta.blackwhitedeliverydriver.entity.Basket;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BasketResponseDto {
    private UUID basketId;

    public static BasketResponseDto from(Basket basket) {
        return BasketResponseDto.builder().basketId(basket.getBasketId()).build();
    }
}
