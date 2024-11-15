package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.ReviewIdResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.ReviewRequestDto;
import com.sparta.blackwhitedeliverydriver.entity.Order;
import com.sparta.blackwhitedeliverydriver.entity.Review;
import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.exception.OrderExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.StoreExceptionMessage;
import com.sparta.blackwhitedeliverydriver.repository.OrderRepository;
import com.sparta.blackwhitedeliverydriver.repository.ReviewRepository;
import com.sparta.blackwhitedeliverydriver.repository.StoreRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public ReviewIdResponseDto createReview(ReviewRequestDto requestDto, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));

        Store store = storeRepository.findById(order.getStore().getStoreId())
                .orElseThrow(() -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage()));

        // 리뷰 총점 업데이트 및 리뷰수 +1
        store.updateRating(requestDto.getRating());
        storeRepository.save(store);

        Review review = Review.from(requestDto, order);
        reviewRepository.save(review);

        return new ReviewIdResponseDto(review.getId());
    }
}
