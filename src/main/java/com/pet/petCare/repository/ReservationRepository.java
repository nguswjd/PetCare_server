package com.pet.petCare.repository;

import com.pet.petCare.domain.Reservation;
import com.pet.petCare.domain.enums.Department;
import com.pet.petCare.domain.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r.reservationTime FROM Reservation r " +
            "WHERE r.hospital.id = :hospitalId " +
            "AND r.reservationDate = :date " +
            "AND r.department = :department " +
            "AND r.status IN ('PENDING', 'CONFIRMED')")
    List<LocalTime> findBookedTimesByHospitalAndDateAndDepartment(
            @Param("hospitalId") Long hospitalId,
            @Param("date") LocalDate date,
            @Param("department") Department department
    );

    boolean existsByHospitalIdAndReservationDateAndReservationTimeAndStatusIn(
            Long hospitalId,
            LocalDate date,
            LocalTime time,
            List<ReservationStatus> statuses
    );

    Optional<Reservation> findByIdAndUserId(Long id, Long userId);
}