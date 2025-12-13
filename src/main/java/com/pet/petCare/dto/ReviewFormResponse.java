package com.pet.petCare.dto;

import com.pet.petCare.domain.Reservation;

import java.time.LocalDate;

public record ReviewFormResponse(
        Long reservationId,
        String hospitalName,
        String hospitalAddress,
        String visitDate,
        String animalType,
        String breed,
        String department
) {
    public static ReviewFormResponse from(Reservation r) {
        return new ReviewFormResponse(
                r.getId(),
                r.getHospital().getName(),
                r.getHospital().getAddress(),
                r.getReservationDate().toString(),
                r.getAnimalType().getDescription(),
                r.getBreed().getDescription(),
                r.getDepartment().name()
        );
    }
}