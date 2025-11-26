package com.pet.petCare.domain;

import jakarta.persistence.Embeddable;
import java.time.LocalTime;

@Embeddable
public class BreakTime {

    private LocalTime startTime;
    private LocalTime endTime;

    public BreakTime() {}

    public BreakTime(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isBreakTime(LocalTime currentTime) {
        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
    }
}