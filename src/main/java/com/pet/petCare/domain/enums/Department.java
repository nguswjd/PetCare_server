package com.pet.petCare.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Department {
    VACCINATION("예방접종"),
    INTERNAL_SURGERY("내과 / 외과"),
    DENT_SKIN_EYE("치과 / 피부과 / 안과"),
    NEUTERING("중성화수술"),
    CHECKUP("건강검진"),
    EMERGENCY("응급진료"),
    ORTHO_NEURO_CENTER("정형외과 / 심장내과 / 중앙클리닉"),
    OTHER("기타");

    private final String department;

    public static Department fromDescription(String description) {
        for (Department dept : Department.values()) {
            if (dept.getDepartment().equals(description)) {
                return dept;
            }
        }
        throw new IllegalArgumentException("Unknown department: " + description);
    }
}