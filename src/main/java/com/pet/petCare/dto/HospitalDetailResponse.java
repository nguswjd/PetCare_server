package com.pet.petCare.dto;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.enums.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class HospitalDetailResponse {
    private Long id;
    private String name;
    private String address;
    private String hospitalNumber;
    private boolean hasParking;
    private List<Department> departments;
    private List<AnimalType> animalTypes;
    private List<Breed> breeds;
    private List<LocalDate> holidays;
    private LocalTime operatingStartTime;
    private LocalTime operatingEndTime;
    private boolean is24Hours;
    private List<LocalTime> breakTimes;
    private HospitalStatus status;
    private String imageUrl;
    private HospitalOperatingStatus operatingStatus;
    private Long reviewCount;

    public static HospitalDetailResponse from(Hospital hospital) {
        return HospitalDetailResponse.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .hospitalNumber(hospital.getHospitalNumber())
                .hasParking(hospital.isHasParking())
                .departments(hospital.getDepartments())
                .animalTypes(hospital.getAnimalTypes())
                .breeds(hospital.getBreeds())
                .holidays(hospital.getHolidays())
                .operatingStartTime(hospital.getOperatingStartTime())
                .operatingEndTime(hospital.getOperatingEndTime())
                .is24Hours(hospital.isIs24Hours())
                .breakTimes(hospital.getBreakTimes())
                .status(hospital.getStatus())
                .imageUrl(hospital.getImageUrl())
                .operatingStatus(hospital.getOperatingStatus())
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
                .departments(hospital.getDepartments())
                .animalTypes(hospital.getAnimalTypes())
                .breeds(hospital.getBreeds())
                .holidays(hospital.getHolidays())
                .operatingStartTime(hospital.getOperatingStartTime())
                .operatingEndTime(hospital.getOperatingEndTime())
                .is24Hours(hospital.isIs24Hours())
                .breakTimes(hospital.getBreakTimes())
                .status(hospital.getStatus())
                .imageUrl(hospital.getImageUrl())
                .operatingStatus(hospital.getOperatingStatus())
                .reviewCount(reviewCount)
                .build();
    }
}