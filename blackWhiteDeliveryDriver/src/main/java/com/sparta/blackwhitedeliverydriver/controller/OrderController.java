package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.OrderGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderResponseDto;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.service.OrderService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Secured({"ROLE_CUSTOMER"})
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        //주문서 생성
        OrderResponseDto response = orderService.createOrder(userDetails.getUsername());
        //201 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_MASTER", "ROLE_MANAGER"})
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderGetResponseDto> getOrderDetail(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @PathVariable UUID orderId) {
        //주문서 상세 조회
        OrderGetResponseDto response = orderService.getOrderDetail(userDetails.getUsername(), orderId);
        //200 반환
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}