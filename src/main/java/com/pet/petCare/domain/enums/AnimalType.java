package com.pet.petCare.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnimalType {
    TERRESTRIAL("육지동물"),
    AQUATIC("수생동물"),
    AVIAN("조류"),
    OTHER("기타");

    private final String description;
}
