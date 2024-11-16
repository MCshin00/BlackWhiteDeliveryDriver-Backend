package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.controller.PayRefundRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.PayApproveResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.PayCancelResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.PayReadyResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.PayRefundResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.PayRequestDto;
import com.sparta.blackwhitedeliverydriver.entity.Order;
import com.sparta.blackwhitedeliverydriver.entity.OrderStatusEnum;
import com.sparta.blackwhitedeliverydriver.entity.Pay;
import com.sparta.blackwhitedeliverydriver.entity.PayStatusEnum;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.OrderExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.PayExceptionMessage;
import com.sparta.blackwhitedeliverydriver.repository.OrderRepository;
import com.sparta.blackwhitedeliverydriver.repository.PayRepository;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
import com.sparta.blackwhitedeliverydriver.util.HttpUtil;
import com.sparta.blackwhitedeliverydriver.util.PayUtil;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayService {
    private final String PAY_URI = "https://open-api.kakaopay.com/online/v1";

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PayRepository payRepository;

    private final PayUtil payUtil;
    private final HttpUtil httpUtil;

    @Transactional
    public PayReadyResponseDto readyToPay(String username, PayRequestDto request) {
        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        //주문 유효성
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));

        //유저와 주문 유저 비교
        checkOrderUser(order, user);

        //주문 상태 체크
        checkOrderStatus(order);

        //파라미터와 헤더 설정
        Map<String, String> parameters = payUtil.getReadyPayParameters(user, order);
        HttpEntity<Map<String, String>> restRequest = httpUtil.getHttpEntity(payUtil.getHeaders(), parameters);

        //카카오페이에 요청
        RestTemplate restTemplate = new RestTemplate();
        PayReadyResponseDto response = restTemplate.postForObject(PAY_URI + "/payment/ready", restRequest,
                PayReadyResponseDto.class);

        assert response != null;
        order.updateTid(response.getTid());

        return response;
    }

    @Transactional
    public PayApproveResponseDto approvePay(String username, String pgToken, String tid) {

        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        //주문 유효성
        Order order = orderRepository.findByTid(tid)
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));

        Map<String, String> parameters = payUtil.getApprovePayParameters(tid, pgToken, order);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, payUtil.getHeaders());

        log.info("auth:{}", requestEntity.getHeaders().get("Authorization"));

        RestTemplate restTemplate = new RestTemplate();
        PayApproveResponseDto approveResponse = restTemplate.postForObject(
                PAY_URI + "/payment/approve", requestEntity, PayApproveResponseDto.class);

        log.info("response:{}", approveResponse);

        assert approveResponse != null;
        Pay pay = Pay.of(order, approveResponse);
        payRepository.save(pay);
        order.updateStatus(OrderStatusEnum.PENDING);

        return approveResponse;
    }

    @Transactional
    public PayRefundResponseDto refundPayment(String username, PayRefundRequestDto request) {
        //환불 조건에 대해서 필수 요구 사항 확인 필요

        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_PUBLIC.getMessage()));

        //주문 유효성
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));

        //유저와 주문 유저의 유효성
        checkOrderUser(order, user);

        //pay 유효성
        Pay pay = payRepository.findByOrder(order)
                .orElseThrow(() -> new NullPointerException(PayExceptionMessage.PAY_NOT_FOUND.getMessage()));

        //100% 환불로 일단 구현
        int cancelAmount = pay.getPayAmount();

        //카카오 페이 서버로 보낼 요청 생성 및 api 호출
        Map<String, String> parameters = payUtil.getRefundParameters(pay, cancelAmount);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, payUtil.getHeaders());
        RestTemplate restTemplate = new RestTemplate();
        PayCancelResponseDto cancelResponse = restTemplate.postForObject(PAY_URI + "/payment/cancel", requestEntity,
                PayCancelResponseDto.class);

        //주문 상태 업데이트
        order.updateStatus(OrderStatusEnum.CANCEL);

        //pay 업데이트
        assert cancelResponse != null;
        pay.updateByRefund(PayStatusEnum.REFUND, cancelResponse.getCanceled_amount().getTotal(),
                cancelResponse.getCanceled_at());

        return new PayRefundResponseDto("주문을 취소했습니다.");
    }

    private void checkOrderUser(Order order, User user) {
        String orderUsername = order.getUser().getUsername();
        String username = user.getUsername();
        if (!orderUsername.equals(username)) {
            throw new IllegalArgumentException(OrderExceptionMessage.ORDER_USER_NOT_EQUALS.getMessage());
        }
    }

    private void checkOrderStatus(Order order) {
        if (!order.getStatus().equals(OrderStatusEnum.CREATE)) {
            throw new IllegalArgumentException(OrderExceptionMessage.ORDER_UNABLE_PAY_STATUS.getMessage());
        }
    }
}
