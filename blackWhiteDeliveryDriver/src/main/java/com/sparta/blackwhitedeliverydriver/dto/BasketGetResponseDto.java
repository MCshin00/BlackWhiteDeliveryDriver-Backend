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
    private UUID storeId;
    private String storeName;
    private UUID productId;
    private Integer quantity;

    public static BasketGetResponseDto fromBasket(Basket basket) {
        return BasketGetResponseDto
                .builder()
                .basketId(basket.getId())
                .username(basket.getUser().getUsername())
                .storeId(basket.getStore().getStoreId())
                .storeName(basket.getStore().getStoreName())
                .productId(basket.getProduct().getProductId())
                .quantity(basket.getQuantity())
                .build();
    }
}
