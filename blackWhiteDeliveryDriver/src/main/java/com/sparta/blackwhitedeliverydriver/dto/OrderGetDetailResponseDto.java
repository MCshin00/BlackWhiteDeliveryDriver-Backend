package com.sparta.blackwhitedeliverydriver.dto;

import com.sparta.blackwhitedeliverydriver.entity.Order;
import com.sparta.blackwhitedeliverydriver.entity.OrderProduct;
import com.sparta.blackwhitedeliverydriver.entity.OrderStatusEnum;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderGetDetailResponseDto {
    private UUID orderId;
    private UUID storeId;
    private String username;
    private OrderStatusEnum status;
    private Integer finalPay;
    private Integer discountRate;
    private Integer discountAmount;
    private List<ProductResponseDto> products;

    public static OrderGetDetailResponseDto of(Order order, List<OrderProduct> orderProducts) {
        return OrderGetDetailResponseDto.builder()
                .orderId(order.getId())
                .storeId(order.getStore().getStoreId())
                .username(order.getUser().getUsername())
                .status(order.getStatus())
                .finalPay(order.getFinalPay())
                .discountRate(order.getDiscountRate())
                .discountAmount(order.getDiscountAmount())
                .products(orderProducts.stream().map(orderProduct -> ProductResponseDto.from(orderProduct.getProduct()))
                        .collect(
                                Collectors.toList()))
                .build();
    }
}
