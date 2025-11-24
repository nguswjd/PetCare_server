package com.pet.petCare.domain.enums;

public enum HospitalStatus {
    PENDING("승인 대기"),
    APPROVED("승인 완료"),
    REJECTED("승인 거부"),
    SUSPENDED("운영 정지");

    private final String description;

    HospitalStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}