package com.pet.petCare.controller;

import com.pet.petCare.dto.ReservationRequest;
import com.pet.petCare.dto.ReservationResponse;
import com.pet.petCare.dto.AvailableTimesResponse;
import com.pet.petCare.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody ReservationRequest requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        ReservationResponse response = reservationService.createReservation(
                requestDto,
                userDetails.getUsername()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{hospitalId}/available-times")
    public ResponseEntity<AvailableTimesResponse> getAvailableTimes(
            @PathVariable Long hospitalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String department) {

        AvailableTimesResponse response = reservationService.getAvailableTimes(
                hospitalId,
                date,
                department
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        ReservationResponse response = reservationService.cancelReservation(
                reservationId,
                userDetails.getUsername()
        );

        return ResponseEntity.ok(response);
    }
}