package com.sparta.blackwhitedeliverydriver.dto;

import com.sparta.blackwhitedeliverydriver.entity.Order;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderGetResponseDto {
    private UUID orderId;
    private String username;
    private Integer finalPay;
    private Integer discountRate;
    private Integer discountAmount;

    public static OrderGetResponseDto fromOrder(Order order){
        return OrderGetResponseDto.builder()
                .orderId(order.getId())
                .username(order.getUser().getUsername())
                .finalPay(order.getFinalPay())
                .discountRate(order.getDiscountRate())
                .discountAmount(order.getDiscountAmount())
                .build();
    }
}
