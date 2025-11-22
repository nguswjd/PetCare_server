package com.pet.petCare.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    PENDING("예약 대기"),
    CONFIRMED("예약 확정"),
    COMPLETED("진료 완료"),
    CANCELLED("예약 취소"),
    NO_SHOW("노쇼");

    private final String description;
}