package com.pet.petCare.dto;

import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.domain.enums.Breed;
import com.pet.petCare.domain.User;
import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        String username,
        String name,
        String phoneNumber,
        AnimalType species,
        Breed breed,
        Boolean marketingConsent,
        String message
) {
    public static AuthResponse success(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .species(user.getSpecies())
                .breed(user.getBreed())
                .marketingConsent(user.getMarketingConsent())
                .build();
    }

    public static AuthResponse success(String message) {
        return AuthResponse.builder()
                .message(message)
                .build();
    }

    public static AuthResponse error(String message) {
        return AuthResponse.builder()
                .message(message)
                .build();
    }
}