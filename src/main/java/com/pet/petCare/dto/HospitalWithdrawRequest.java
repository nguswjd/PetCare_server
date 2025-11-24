package com.pet.petCare.dto;

public record HospitalWithdrawRequest(
        String password
) {
    public String password() {
        return password;
    }
}