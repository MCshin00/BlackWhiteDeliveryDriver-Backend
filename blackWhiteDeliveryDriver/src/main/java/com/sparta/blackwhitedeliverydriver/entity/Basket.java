package com.sparta.blackwhitedeliverydriver.entity;

import com.sparta.blackwhitedeliverydriver.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketUpdateRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID basketId;
    private UUID productId; // 임시 컬럼
    private Integer quantity;

    public void updateBasketOfQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public static Basket from(BasketAddRequestDto requestDto) {
        return Basket.builder()
                .productId(UUID.fromString(requestDto.getProductId()))
                .quantity(requestDto.getQuantity())
                .build();
    }

}
