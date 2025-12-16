package com.pet.petCare.dto;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.enums.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class HospitalDetailResponse {
    private Long id;
    private String name;
    private String address;
    private String hospitalNumber;
    private boolean hasParking;
    private List<String> departments;
    private List<String> animalTypes;
    private List<String> breeds;
    private List<LocalDate> holidays;
    private LocalTime operatingStartTime;
    private LocalTime operatingEndTime;
    private boolean is24Hours;
    private List<LocalTime> breakTimes;
    private String status;
    private String imageUrl;
    private String operatingStatus;
    private Long reviewCount;

    public static HospitalDetailResponse from(Hospital hospital) {
        return HospitalDetailResponse.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .hospitalNumber(hospital.getHospitalNumber())
                .hasParking(hospital.isHasParking())
                .departments(hospital.getDepartments().stream()
                        .map(Department::getDepartment)
                        .collect(Collectors.toList()))
                .animalTypes(hospital.getAnimalTypes().stream()
                        .map(AnimalType::getDescription)
                        .collect(Collectors.toList()))
                .breeds(hospital.getBreeds().stream()
                        .map(Breed::getDescription)
                        .collect(Collectors.toList()))
                .holidays(hospital.getHolidays())
                .operatingStartTime(hospital.getOperatingStartTime())
                .operatingEndTime(hospital.getOperatingEndTime())
                .is24Hours(hospital.isIs24Hours())
                .breakTimes(hospital.getBreakTimes())
                .status(hospital.getStatus() != null ? hospital.getStatus().name() : null)
                .imageUrl(hospital.getImageUrl())
                .operatingStatus(hospital.getOperatingStatus() != null ?
                        hospital.getOperatingStatus().getDescription() : null)
                .reviewCount(0L)
                .build();
    }

    public static HospitalDetailResponse from(Hospital hospital, Long reviewCount) {
        return HospitalDetailResponse.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .hospitalNumber(hospital.getHospitalNumber())
                .hasParking(hospital.isHasParking())
                .departments(hospital.getDepartments().stream()
                        .map(Department::getDepartment)
                        .collect(Collectors.toList()))
                .animalTypes(hospital.getAnimalTypes().stream()
                        .map(AnimalType::getDescription)
                        .collect(Collectors.toList()))
                .breeds(hospital.getBreeds().stream()
                        .map(Breed::getDescription)
                        .collect(Collectors.toList()))
                .holidays(hospital.getHolidays())
                .operatingStartTime(hospital.getOperatingStartTime())
                .operatingEndTime(hospital.getOperatingEndTime())
                .is24Hours(hospital.isIs24Hours())
                .breakTimes(hospital.getBreakTimes())
                .status(hospital.getStatus() != null ? hospital.getStatus().name() : null)
                .imageUrl(hospital.getImageUrl())
                .operatingStatus(hospital.getOperatingStatus() != null ?
                        hospital.getOperatingStatus().getDescription() : null)
                .reviewCount(reviewCount)
                .build();
    }
}