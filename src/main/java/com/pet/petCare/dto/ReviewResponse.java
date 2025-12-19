package com.pet.petCare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private Long hospitalId;
    private String hospitalName;
    private String username;
    private String department;
    private String animalType;
    private String breed;
    private String content;
    private LocalDate visitDate;
    private LocalDate createdDate;
    private Boolean revisitIntention;
    private Boolean isMyReview;
}