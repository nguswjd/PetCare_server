package com.pet.petCare.dto;

public record HospitalUpdateRequest(
        String representativeName,
        String name,
        String hospitalNumber,
        String address
) {
    public String getRepresentativeName() {
        return representativeName;
    }

    public String getName() {
        return name;
    }

    public String getHospitalNumber() {
        return hospitalNumber;
    }

    public String getAddress() {
        return address;
    }
}