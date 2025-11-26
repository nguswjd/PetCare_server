package com.pet.petCare.dto;

import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.domain.enums.Breed;
import com.pet.petCare.domain.enums.Department;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record HospitalSignupRequest(
        String representativeName,
        String username,
        String password,
        String name,
        String hospitalNumber,
        String businessRegistrationNumber,
        String address,

        Boolean hasParking,
        List<Department> departments,
        List<AnimalType> animalTypes,
        List<Breed> breeds,
        List<LocalDate> holidays,

        LocalTime operatingStartTime,
        LocalTime operatingEndTime,
        Boolean is24Hours,
        List<LocalTime> breakTimes,

        String imageUrl,
        String description
) {}