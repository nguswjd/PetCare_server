package com.pet.petCare.dto;

import com.pet.petCare.domain.Reservation;
import com.pet.petCare.domain.enums.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class HospitalReservationResponse {
    private Long reservationId;
    private String reserverName;
    private String userPhoneNumber;
    private String animalType;
    private String animalTypeDescription;
    private String breed;
    private String breedDescription;
    private Integer age;
    private Integer weight;
    private String department;
    private LocalDate date;
    private LocalTime time;
    private String status;

    public static HospitalReservationResponse from(Reservation reservation) {
        String currentStatus = reservation.getStatus().name();

        LocalDateTime reservationDateTime = LocalDateTime.of(reservation.getReservationDate(), reservation.getReservationTime());

        if (reservationDateTime.isBefore(LocalDateTime.now()) &&
                (reservation.getStatus() == ReservationStatus.PENDING || reservation.getStatus() == ReservationStatus.CONFIRMED)) {
            currentStatus = "NO_SHOW";
        }

        return HospitalReservationResponse.builder()
                .reservationId(reservation.getId())
                .reserverName(reservation.getReserverName())
                .userPhoneNumber(reservation.getUser().getPhoneNumber())
                .animalType(reservation.getAnimalType().name())
                .animalTypeDescription(reservation.getAnimalType().getDescription())
                .breed(reservation.getBreed().name())
                .breedDescription(reservation.getBreed().getDescription())
                .age(reservation.getAge())
                .weight(reservation.getWeight())
                .department(reservation.getDepartment().getDepartment())
                .date(reservation.getReservationDate())
                .time(reservation.getReservationTime())
                .status(currentStatus)
                .build();
    }
}