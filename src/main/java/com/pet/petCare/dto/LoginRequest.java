package com.pet.petCare.dto;

public record LoginRequest(
        String username,
        String password
) {
}