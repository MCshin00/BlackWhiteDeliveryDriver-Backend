package com.sparta.blackwhitedeliverydriver.dto;

import com.sparta.blackwhitedeliverydriver.entity.Order;
import com.sparta.blackwhitedeliverydriver.entity.OrderStatusEnum;
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
    private UUID storeId;
    private String username;
    private OrderStatusEnum status;
    private Integer finalPay;
    private Integer discountRate;
    private Integer discountAmount;

    public static OrderGetResponseDto fromOrder(Order order) {
        return OrderGetResponseDto.builder()
                .orderId(order.getId())
                .storeId(order.getStore().getStoreId())
                .username(order.getUser().getUsername())
                .status(order.getStatus())
                .finalPay(order.getFinalPay())
                .discountRate(order.getDiscountRate())
                .discountAmount(order.getDiscountAmount())
                .build();
    }
}
