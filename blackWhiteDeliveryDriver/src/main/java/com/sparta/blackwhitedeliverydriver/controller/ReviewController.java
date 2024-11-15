package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.ReviewIdResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.ReviewRequestDto;
import com.sparta.blackwhitedeliverydriver.service.ReviewService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{orderId}")
    public ResponseEntity<ReviewIdResponseDto> createReview(@Valid @RequestBody ReviewRequestDto requestDto, @PathVariable UUID orderId) {
        ReviewIdResponseDto responseDto = reviewService.createReview(requestDto, orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
