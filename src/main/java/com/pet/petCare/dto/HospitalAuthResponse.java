package com.pet.petCare.dto;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.enums.HospitalStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record HospitalAuthResponse(
        String token,
        String username,
        String representativeName,
        String name,
        String hospitalNumber,
        String businessRegistrationNumber,
        String address,
        HospitalStatus status,
        Boolean hasParking,
        List<EnumInfo> departments,
        List<EnumInfo> animalTypes,
        List<EnumInfo> breeds,
        List<LocalDate> holidays,
        LocalTime operatingStartTime,
        LocalTime operatingEndTime,
        Boolean is24Hours,
        List<LocalTime> breakTimes,
        String imageUrl,
        String message
) {
    @Builder
    public record EnumInfo(String code, String label) {}

    public static HospitalAuthResponse success(String token, Hospital hospital) {
        return HospitalAuthResponse.builder()
                .token(token)
                .username(hospital.getUsername())
                .representativeName(hospital.getRepresentativeName())
                .name(hospital.getName())
                .hospitalNumber(hospital.getHospitalNumber())
                .businessRegistrationNumber(hospital.getBusinessRegistrationNumber())
                .address(hospital.getAddress())
                .status(hospital.getStatus())
                .hasParking(hospital.isHasParking())
                .departments(hospital.getDepartments().stream()
                        .map(dept -> new EnumInfo(dept.name(), dept.getDepartment()))
                        .collect(Collectors.toList()))
                .animalTypes(hospital.getAnimalTypes().stream()
                        .map(type -> new EnumInfo(type.name(), type.getDescription()))
                        .collect(Collectors.toList()))
                .breeds(hospital.getBreeds().stream()
                        .map(breed -> new EnumInfo(breed.name(), breed.getDescription()))
                        .collect(Collectors.toList()))
                .holidays(hospital.getHolidays())
                .operatingStartTime(hospital.getOperatingStartTime())
                .operatingEndTime(hospital.getOperatingEndTime())
                .is24Hours(hospital.isIs24Hours())
                .breakTimes(hospital.getBreakTimes())
                .imageUrl(hospital.getImageUrl())
                .build();
    }

    public static HospitalAuthResponse success(String message) {
        return HospitalAuthResponse.builder()
                .message(message)
                .build();
    }

    public static HospitalAuthResponse error(String message) {
        return HospitalAuthResponse.builder()
                .message(message)
                .build();
    }
}