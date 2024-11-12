package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.OrderResponseDto;
import com.sparta.blackwhitedeliverydriver.repository.BasketRepository;
import com.sparta.blackwhitedeliverydriver.repository.OrderProductRepository;
import com.sparta.blackwhitedeliverydriver.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final BasketRepository basketRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    @Transactional
    public OrderResponseDto createOrder(String username) {
        //유저 유효성 검사
        //order 엔티티 생성 및 저장
        //유저와 관련된 장바구니 품목 찾기
        //연관관계 테이블에 장바구니 품목 저장
        //최종금액 계산 및 업데이트
        return null;
    }
}
