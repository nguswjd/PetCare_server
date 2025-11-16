package com.pet.petCare.dto;

import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.domain.enums.Breed;

public record SignupRequest(
        String name,
        String username,
        String password,
        String phoneNumber,
        AnimalType species,
        Breed breed,
        Boolean marketingConsent
) {
}
