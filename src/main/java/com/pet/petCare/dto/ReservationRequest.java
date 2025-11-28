package com.pet.petCare.dto;

import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.domain.enums.Breed;
import com.pet.petCare.domain.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequest {

    private Long hospitalId;
    private String reserverName;
    private AnimalType animalType;
    private Breed breed;
    private Integer age;
    private Integer weight;
    private Department department;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
}