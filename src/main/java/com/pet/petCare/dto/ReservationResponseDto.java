package com.pet.petCare.dto;

import com.pet.petCare.domain.Reservation;
import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.domain.enums.Breed;
import com.pet.petCare.domain.enums.Department;
import com.pet.petCare.domain.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDto {

    private Long id;
    private Long userId;
    private Long hospitalId;
    private String hospitalName;
    private String reserverName;
    private AnimalType animalType;
    private Breed breed;
    private Integer age;
    private Integer weight;
    private Department department;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private ReservationStatus status;
    private LocalDateTime createdAt;

    public static ReservationResponseDto from(Reservation reservation) {
        return ReservationResponseDto.builder()
                .id(reservation.getId())
                .userId(reservation.getUser().getId())
                .hospitalId(reservation.getHospital().getId())
                .hospitalName(reservation.getHospital().getName())
                .reserverName(reservation.getReserverName())
                .animalType(reservation.getAnimalType())
                .breed(reservation.getBreed())
                .age(reservation.getAge())
                .weight(reservation.getWeight())
                .department(reservation.getDepartment())
                .reservationDate(reservation.getReservationDate())
                .reservationTime(reservation.getReservationTime())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}