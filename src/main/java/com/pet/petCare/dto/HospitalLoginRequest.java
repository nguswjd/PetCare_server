package com.pet.petCare.dto;

public record HospitalLoginRequest(
        String username,
        String password
) {}