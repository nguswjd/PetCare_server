package com.pet.petCare.dto;

import java.time.LocalTime;

public record BreakTime(
        LocalTime startTime,
        LocalTime endTime
) {
    public static BreakTime from(com.pet.petCare.domain.BreakTime breakTime) {
        return new BreakTime(
                breakTime.getStartTime(),
                breakTime.getEndTime()
        );
    }

    public com.pet.petCare.domain.BreakTime toEntity() {
        return new com.pet.petCare.domain.BreakTime(startTime, endTime);
    }
}