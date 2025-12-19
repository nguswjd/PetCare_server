package com.pet.petCare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedHospitalResponse {
    private Long id;
    private String name;
    private String address;
    private String imageUrl;
    private String operatingStatus;
    private int recommendationScore;
    private String recommendationReason;
}