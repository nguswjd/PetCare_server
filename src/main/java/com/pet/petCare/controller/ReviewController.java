package com.pet.petCare.controller;

import com.pet.petCare.dto.ReviewFormResponse;
import com.pet.petCare.dto.ReviewRequest;
import com.pet.petCare.dto.ReviewResponse;
import com.pet.petCare.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/form/{reservationId}")
    public ResponseEntity<ReviewFormResponse> getReviewForm(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ReviewFormResponse response = reviewService.getReviewFormInfo(reservationId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<String> createReview(
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        reviewService.createReview(request, userDetails.getUsername());
        return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");
    }

    @GetMapping("/check-available")
    public ResponseEntity<Long> checkReviewAvailable(
            @RequestParam Long hospitalId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long reservationId = reviewService.getReviewableReservationId(
                hospitalId,
                userDetails.getUsername()
        );
        return ResponseEntity.ok(reservationId);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<ReviewResponse> responses = reviewService.getMyReviews(userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        reviewService.deleteReview(reviewId, userDetails.getUsername());
        return ResponseEntity.ok("리뷰가 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<ReviewResponse>> getHospitalReviews(
            @PathVariable Long hospitalId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String currentUsername = (userDetails != null) ? userDetails.getUsername() : null;
        List<ReviewResponse> responses = reviewService.getHospitalReviews(hospitalId, currentUsername);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/hospital/my")
    public ResponseEntity<List<ReviewResponse>> getMyHospitalReviews(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<ReviewResponse> responses = reviewService.getMyHospitalReviews(userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }
}