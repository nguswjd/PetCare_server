package com.pet.petCare.dto;

import com.pet.petCare.domain.BreakTime;
import java.time.LocalTime;

public record BreakTimeDto(
        LocalTime startTime,
        LocalTime endTime
) {
    public static BreakTimeDto from(BreakTime breakTime) {
        return new BreakTimeDto(
                breakTime.getStartTime(),
                breakTime.getEndTime()
        );
    }

    public BreakTime toEntity() {
        return new BreakTime(startTime, endTime);
    }
}