package com.pet.petCare.dto;

import com.pet.petCare.domain.Hospital;
import lombok.Builder;

import java.util.List;

@Builder
public record HospitalDetailResponse(
        boolean hasParking,
        String address,
        List<String> animalTypes,
        List<String> departments
) {
    public static HospitalDetailResponse from(Hospital hospital) {
        return HospitalDetailResponse.builder()
                .hasParking(hospital.isHasParking())
                .address(hospital.getAddress())
                .animalTypes(hospital.getAnimalTypes().stream()
                        .map(at -> at.getAnimalType().getDescription())
                        .toList())
                .departments(hospital.getDepartments().stream()
                        .map(d -> d.getDepartment().getDepartment())
                        .toList())
                .build();
    }
}