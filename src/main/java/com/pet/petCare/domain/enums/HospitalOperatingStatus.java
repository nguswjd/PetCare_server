package com.pet.petCare.domain.enums;

public enum HospitalOperatingStatus {
    OPEN("운영 중"),
    CLOSED("운영 종료"),
    BREAK("휴게 중");

    private final String description;

    HospitalOperatingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}