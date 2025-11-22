package com.pet.petCare.service;

import com.pet.petCare.domain.Reservation;
import com.pet.petCare.domain.User;
import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.enums.Department;
import com.pet.petCare.domain.enums.ReservationStatus;
import com.pet.petCare.dto.ReservationRequestDto;
import com.pet.petCare.dto.ReservationResponseDto;
import com.pet.petCare.dto.AvailableTimesResponseDto;
import com.pet.petCare.repository.ReservationRepository;
import com.pet.petCare.repository.UserRepository;
import com.pet.petCare.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;

    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto requestDto, String username) {
        validateReservationRequest(requestDto);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Hospital hospital = hospitalRepository.findById(requestDto.getHospitalId())
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));

        if (requestDto.getReservationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("과거 날짜로는 예약할 수 없습니다.");
        }

        boolean isDuplicate = reservationRepository.existsByHospitalIdAndReservationDateAndReservationTimeAndStatusIn(
                requestDto.getHospitalId(),
                requestDto.getReservationDate(),
                requestDto.getReservationTime(),
                Arrays.asList(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
        );

        if (isDuplicate) {
            throw new IllegalStateException("이미 예약된 시간입니다.");
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .hospital(hospital)
                .reserverName(requestDto.getReserverName())
                .animalType(requestDto.getAnimalType())
                .breed(requestDto.getBreed())
                .age(requestDto.getAge())
                .weight(requestDto.getWeight())
                .department(requestDto.getDepartment())
                .reservationDate(requestDto.getReservationDate())
                .reservationTime(requestDto.getReservationTime())
                .status(ReservationStatus.PENDING)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponseDto.from(savedReservation);
    }

    public AvailableTimesResponseDto getAvailableTimes(Long hospitalId, LocalDate date, String departmentStr) {
        hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));

        Department department = Department.valueOf(departmentStr);

        List<LocalTime> bookedTimes = reservationRepository
                .findBookedTimesByHospitalAndDateAndDepartment(hospitalId, date, department);

        List<LocalTime> allTimes = new ArrayList<>();
        for (int hour = 8; hour < 21; hour++) {
            allTimes.add(LocalTime.of(hour, 0));
        }

        List<LocalTime> availableTimes = allTimes.stream()
                .filter(time -> !bookedTimes.contains(time))
                .collect(Collectors.toList());

        return AvailableTimesResponseDto.builder()
                .hospitalId(hospitalId)
                .date(date)
                .department(department)
                .departmentDescription(department.getDepartment())
                .availableTimes(availableTimes)
                .bookedTimes(bookedTimes)
                .build();
    }

    @Transactional
    public ReservationResponseDto cancelReservation(Long reservationId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        reservation.cancel();

        return ReservationResponseDto.from(reservation);
    }

    private void validateReservationRequest(ReservationRequestDto requestDto) {
        if (requestDto.getHospitalId() == null) {
            throw new IllegalArgumentException("병원 ID는 필수입니다.");
        }
        if (requestDto.getReserverName() == null || requestDto.getReserverName().trim().isEmpty()) {
            throw new IllegalArgumentException("예약자명은 필수입니다.");
        }
        if (requestDto.getAnimalType() == null) {
            throw new IllegalArgumentException("동물 종류는 필수입니다.");
        }
        if (requestDto.getBreed() == null) {
            throw new IllegalArgumentException("품종은 필수입니다.");
        }
        if (requestDto.getAge() == null || requestDto.getAge() < 0 || requestDto.getAge() > 30) {
            throw new IllegalArgumentException("나이는 0-30 사이여야 합니다.");
        }
        if (requestDto.getWeight() == null || requestDto.getWeight() < 1 || requestDto.getWeight() > 100) {
            throw new IllegalArgumentException("체중은 1-100kg 사이여야 합니다.");
        }
        if (requestDto.getDepartment() == null) {
            throw new IllegalArgumentException("진료 과목은 필수입니다.");
        }
        if (requestDto.getReservationDate() == null) {
            throw new IllegalArgumentException("예약 날짜는 필수입니다.");
        }
        if (requestDto.getReservationTime() == null) {
            throw new IllegalArgumentException("예약 시간은 필수입니다.");
        }
    }
}