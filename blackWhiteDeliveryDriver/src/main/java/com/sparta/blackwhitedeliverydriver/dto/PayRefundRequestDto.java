package com.sparta.blackwhitedeliverydriver.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayRefundRequestDto {
    private UUID orderId;
}
