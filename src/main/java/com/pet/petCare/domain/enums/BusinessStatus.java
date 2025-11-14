package com.pet.petCare.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessStatus {
    OPEN("영업 중"),
    CLOSE("영업 종료"),
    TWENTY_FOUR_HOURS("24시간 영업");

    private final String description;
}