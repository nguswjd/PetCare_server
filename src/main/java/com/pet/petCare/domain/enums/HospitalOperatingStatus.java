package com.pet.petCare.domain.enums;

public enum HospitalOperatingStatus {
    OPEN("운영 중"),
    OPEN_24H("24시간 운영"),
    CLOSED("운영 종료"),
    BREAK("휴게 시간");

    private final String description;

    HospitalOperatingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}