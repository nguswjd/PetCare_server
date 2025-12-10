package com.pet.petCare.dto;

public record HospitalSignupRequest(
        String representativeName,
        String username,
        String password,
        String name,
        String hospitalNumber,
        String businessRegistrationNumber,
        String address,
        Boolean hasParking,
        String departments,
        String animalTypes,
        String breeds,
        String holidays,
        String operatingStartTime,
        String operatingEndTime,
        Boolean is24Hours,
        String breakTimes,
        String imageUrl
) {}