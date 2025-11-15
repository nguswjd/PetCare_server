package com.pet.petCare.dto;

import com.pet.petCare.domain.Hospital;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record HospitalDetailResponse(
        String name,
        String status,
        LocalDate holiday,
        boolean hasParking,
        String address,
        List<String> animalTypes,
        List<String> departments
) {
    public static HospitalDetailResponse from(Hospital hospital) {
        return HospitalDetailResponse.builder()
                .name(hospital.getName())
                .status(hospital.getStatus() != null ? hospital.getStatus().name() : null)
                .holiday(hospital.getHoliday())
                .hasParking(hospital.isHasParking())
                .address(hospital.getAddress())
                .animalTypes(hospital.getAnimalTypes() != null
                        ? hospital.getAnimalTypes().stream()
                        .map(at -> at.getAnimalType().getDescription())
                        .toList()
                        : List.of())
                .departments(hospital.getDepartments() != null
                        ? hospital.getDepartments().stream()
                        .map(d -> d.getDepartment().getDepartment())
                        .toList()
                        : List.of())
                .build();
    }
}
