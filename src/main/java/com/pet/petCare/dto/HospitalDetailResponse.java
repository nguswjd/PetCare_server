package com.pet.petCare.dto;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.HospitalAnimalType;
import com.pet.petCare.domain.HospitalDepartment;
import lombok.Builder;

import java.util.List;

@Builder
public record HospitalDetailResponse(
        boolean hasParking,
        String address,
        List<HospitalAnimalType> animalTypes,
        List<HospitalDepartment> departments
) {
    public static HospitalDetailResponse from(Hospital hospital) {
        return HospitalDetailResponse.builder()
                .hasParking(hospital.isHasParking())
                .animalTypes(hospital.getAnimalTypes())
                .departments(hospital.getDepartments())
                .address(hospital.getAddress())
                .build();
    }
}