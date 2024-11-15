package com.sparta.blackwhitedeliverydriver.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BasketAddRequestDto {
    @NotNull
    private UUID productId; // 음식은 가상의 id로 입력
    @NotNull
    private UUID storeId; //원래 음식에 접근해서 점포 정보를 가져오려고 했지만 빠른 연동을 위해 장바구니와 점포를 연동했습니다.
    @NotNull
    @Min(value = 1)
    @Max(value = 99)
    private Integer quantity;
}
