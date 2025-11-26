package com.pet.petCare.dto;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.domain.enums.Breed;
import com.pet.petCare.domain.enums.Department;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record HospitalDetailResponse(
        String name,
        String approvalStatus,
        String operatingStatus,
        boolean hasParking,
        String address,
        List<String> animalTypes,
        List<String> departments,
        List<String> breeds,
        List<LocalDate> holidays,
        LocalTime operatingStartTime,
        LocalTime operatingEndTime,
        boolean is24Hours,
        List<LocalTime> breakTimes,
        String imageUrl,
        String description
) {
    public static HospitalDetailResponse from(Hospital hospital) {
        return HospitalDetailResponse.builder()
                .name(hospital.getName())
                .imageUrl(hospital.getImageUrl())
                .description(hospital.getDescription())
                .approvalStatus(hospital.getStatus() != null
                        ? hospital.getStatus().getDescription()
                        : null)
                .operatingStatus(hospital.getOperatingStatus().getDescription())
                .hasParking(hospital.isHasParking())
                .address(hospital.getAddress())
                .animalTypes(hospital.getAnimalTypes() != null
                        ? hospital.getAnimalTypes().stream()
                        .map(AnimalType::getDescription)
                        .collect(Collectors.toList())
                        : List.of())
                .departments(hospital.getDepartments() != null
                        ? hospital.getDepartments().stream()
                        .map(Department::getDepartment)
                        .collect(Collectors.toList())
                        : List.of())
                .breeds(hospital.getBreeds() != null
                        ? hospital.getBreeds().stream()
                        .map(Breed::getDescription)
                        .collect(Collectors.toList())
                        : List.of())
                .holidays(hospital.getHolidays() != null
                        ? hospital.getHolidays()
                        : List.of())
                .operatingStartTime(hospital.getOperatingStartTime())
                .operatingEndTime(hospital.getOperatingEndTime())
                .is24Hours(hospital.isIs24Hours())
                .breakTimes(hospital.getBreakTimes() != null
                        ? hospital.getBreakTimes()
                        : List.of())
                .build();
    }
}