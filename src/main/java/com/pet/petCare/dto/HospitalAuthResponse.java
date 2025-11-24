package com.pet.petCare.dto;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.domain.enums.Breed;
import com.pet.petCare.domain.enums.Department;
import com.pet.petCare.domain.enums.HospitalStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
        List<Department> departments,
        List<AnimalType> animalTypes,
        List<Breed> breeds,
        List<LocalDate> holidays,  // LocalDate로 변경
        List<LocalTime> operatingHours,
        String imageUrl,
        String description,
        String message
) {
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
                .departments(hospital.getDepartments())
                .animalTypes(hospital.getAnimalTypes())
                .breeds(hospital.getBreeds())
                .holidays(hospital.getHolidays())
                .operatingHours(hospital.getOperatingHours())
                .imageUrl(hospital.getImageUrl())
                .description(hospital.getDescription())
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