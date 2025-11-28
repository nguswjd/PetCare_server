package com.pet.petCare.dto;

import com.pet.petCare.domain.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableTimesResponse {

    private Long hospitalId;
    private LocalDate date;
    private Department department;
    private String departmentDescription;
    private List<LocalTime> availableTimes;
    private List<LocalTime> bookedTimes;
}