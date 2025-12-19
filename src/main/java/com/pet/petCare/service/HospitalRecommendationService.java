package com.pet.petCare.service;

import com.pet.petCare.domain.*;
import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.domain.enums.Breed;
import com.pet.petCare.domain.enums.Department;
import com.pet.petCare.domain.enums.ReservationStatus;
import com.pet.petCare.dto.RecommendedHospitalResponse;
import com.pet.petCare.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalRecommendationService {

    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final ViewHistoryRepository viewHistoryRepository;

    public RecommendedHospitalResponse recommendHospital(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Hospital> allHospitals = hospitalRepository.findAll();

        if (allHospitals.isEmpty()) {
            throw new IllegalArgumentException("등록된 병원이 없습니다.");
        }

        List<HospitalScore> hospitalScores = allHospitals.stream()
                .map(hospital -> calculateHospitalScore(hospital, user))
                .sorted(Comparator.comparingInt(HospitalScore::getScore).reversed())
                .limit(10)
                .collect(Collectors.toList());

        if (hospitalScores.isEmpty()) {
            throw new IllegalArgumentException("추천할 병원이 없습니다.");
        }

        HospitalScore topHospital = hospitalScores.get(0);

        return RecommendedHospitalResponse.builder()
                .id(topHospital.getHospital().getId())
                .name(topHospital.getHospital().getName())
                .address(extractShortAddress(topHospital.getHospital().getAddress()))
                .imageUrl(topHospital.getHospital().getImageUrl())
                .operatingStatus(topHospital.getHospital().getOperatingStatus().getDescription())
                .recommendationScore(topHospital.getScore())
                .recommendationReason(topHospital.getReason())
                .build();
    }

    private HospitalScore calculateHospitalScore(Hospital hospital, User user) {
        int totalScore = 0;
        List<String> reasons = new ArrayList<>();

        if (user.getSpecies() != null && hospital.getAnimalTypes().contains(user.getSpecies())) {
            totalScore += 50;
            reasons.add("반려동물 종류 진료 가능");
        }

        if (user.getBreed() != null && hospital.getBreeds().contains(user.getBreed())) {
            totalScore += 30;
            reasons.add("품종 전문 진료");
        }

        Long reviewCount = reviewRepository.countByHospitalId(hospital.getId());
        int reviewScore = Math.min(reviewCount.intValue() * 5, 50);
        totalScore += reviewScore;
        if (reviewScore > 0) {
            reasons.add("리뷰 " + reviewCount + "개");
        }

        List<Review> reviews = reviewRepository.findAllByHospitalOrderByCreatedAtDesc(hospital);
        long revisitCount = reviews.stream()
                .filter(Review::isRevisitIntention)
                .count();
        int revisitScore = Math.min((int) revisitCount * 10, 50);
        totalScore += revisitScore;
        if (revisitScore > 0) {
            reasons.add("재방문율 높음");
        }

        List<Reservation> userReservations = reservationRepository.findByUserId(user.getId());
        Set<Department> userDepartments = userReservations.stream()
                .map(Reservation::getDepartment)
                .collect(Collectors.toSet());

        boolean hasSameDepartment = userDepartments.stream()
                .anyMatch(dept -> hospital.getDepartments().contains(dept));

        if (hasSameDepartment) {
            totalScore += 20;
            reasons.add("이전 이용 진료과 보유");
        }

        String operatingStatus = hospital.getOperatingStatus().name();
        if (operatingStatus.equals("OPEN") || operatingStatus.equals("OPEN_24H")) {
            totalScore += 15;
            reasons.add("현재 영업 중");
        }

        if (hospital.isIs24Hours()) {
            totalScore += 10;
            reasons.add("24시간 운영");
        }

        if (hospital.isHasParking()) {
            totalScore += 5;
            reasons.add("주차 가능");
        }

        List<ViewHistory> recentViews = viewHistoryRepository.findByUserIdOrderByViewedAtDesc(
                user.getId(), PageRequest.of(0, 5));
        boolean recentlyViewed = recentViews.stream()
                .anyMatch(vh -> vh.getHospitalId().equals(hospital.getId()));

        if (recentlyViewed) {
            totalScore += 15;
            reasons.add("최근 관심 병원");
        }

        String reasonText = reasons.isEmpty() ? "기본 추천" : String.join(", ", reasons);

        return new HospitalScore(hospital, totalScore, reasonText);
    }

    private String extractShortAddress(String fullAddress) {
        if (fullAddress == null) return "";
        String[] parts = fullAddress.split(" ");
        if (parts.length >= 2) {
            return parts[0] + " " + parts[1];
        }
        return fullAddress;
    }

    private static class HospitalScore {
        private final Hospital hospital;
        private final int score;
        private final String reason;

        public HospitalScore(Hospital hospital, int score, String reason) {
            this.hospital = hospital;
            this.score = score;
            this.reason = reason;
        }

        public Hospital getHospital() {
            return hospital;
        }

        public int getScore() {
            return score;
        }

        public String getReason() {
            return reason;
        }
    }
}