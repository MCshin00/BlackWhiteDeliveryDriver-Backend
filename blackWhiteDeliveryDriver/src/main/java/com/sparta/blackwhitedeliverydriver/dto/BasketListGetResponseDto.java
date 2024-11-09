package com.sparta.blackwhitedeliverydriver.dto;

import com.sparta.blackwhitedeliverydriver.entity.Basket;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BasketListGetResponseDto {
    private List<BasketGetResponseDto> basketList;

    public static BasketListGetResponseDto from(List<Basket> basketList) {
        return BasketListGetResponseDto.builder()
                .basketList(basketList.stream()
                        .map(BasketGetResponseDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
