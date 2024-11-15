package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.ReviewIdResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.ReviewRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.ReviewResponseDto;
import com.sparta.blackwhitedeliverydriver.service.ReviewService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{orderId}")
    public ResponseEntity<ReviewIdResponseDto> createReview(@Valid @RequestBody ReviewRequestDto requestDto,
                                                            @PathVariable UUID orderId) {
        ReviewIdResponseDto responseDto = reviewService.createReview(requestDto, orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/store")
    public ResponseEntity<Page<ReviewResponseDto>> getAllReviewsByStoreId(
            @RequestParam UUID storeId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc) {
        Page<ReviewResponseDto> responseDtos = reviewService.getAllReviewsByStoreId(
                storeId, page - 1, size, sortBy, isAsc);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<ReviewResponseDto>> getAllReviewsByUsername(
            @RequestParam String username,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc) {
        Page<ReviewResponseDto> responseDtos = reviewService.getAllReviewsByUsername(
                username, page - 1, size, sortBy, isAsc);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }
}
