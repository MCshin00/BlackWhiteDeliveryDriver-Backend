package com.sparta.blackwhitedeliverydriver.dto;

import com.sparta.blackwhitedeliverydriver.entity.Basket;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
public class BasketGetResponseDto {
    private UUID basketId;
    private String username;
    private UUID productId;
    private Integer quantity;

    public static BasketGetResponseDto from(Basket basket) {
        return BasketGetResponseDto
                .builder()
                .basketId(basket.getId())
                .username(basket.getUser().getUsername())
                .productId(basket.getProductId())
                .quantity(basket.getQuantity())
                .build();
    }
}
