package com.pet.petCare.dto;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.enums.HospitalOperatingStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HospitalSummaryResponse {
    private Long id;
    private String name;
    private String address;
    private String imageUrl;
    private Long reviewCount;
    private HospitalOperatingStatus operatingStatus;

    public static HospitalSummaryResponse from(Hospital hospital, Long reviewCount) {
        return HospitalSummaryResponse.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .imageUrl(hospital.getImageUrl())
                .reviewCount(reviewCount)
                .operatingStatus(hospital.getOperatingStatus())
                .build();
    }
}