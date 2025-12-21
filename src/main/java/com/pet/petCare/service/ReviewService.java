package com.pet.petCare.service;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.Reservation;
import com.pet.petCare.domain.Review;
import com.pet.petCare.domain.User;
import com.pet.petCare.domain.enums.Department;
import com.pet.petCare.domain.enums.ReservationStatus;
import com.pet.petCare.dto.ReviewFormResponse;
import com.pet.petCare.dto.ReviewRequest;
import com.pet.petCare.dto.ReviewResponse;
import com.pet.petCare.repository.HospitalRepository;
import com.pet.petCare.repository.ReservationRepository;
import com.pet.petCare.repository.ReviewRepository;
import com.pet.petCare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;

    public List<ReviewResponse> getMyReviews(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Review> reviews = reviewRepository.findAllByUserOrderByCreatedAtDesc(user);

        return reviews.stream()
                .map(review -> ReviewResponse.builder()
                        .reviewId(review.getId())
                        .hospitalId(review.getHospital().getId())
                        .hospitalName(review.getHospital().getName())
                        .hospitalImageUrl(review.getHospital().getImageUrl())
                        .department(review.getDepartment().getDepartment())
                        .animalType(review.getReservation().getAnimalType().getDescription())
                        .breed(review.getReservation().getBreed().getDescription())
                        .content(review.getContent())
                        .visitDate(review.getReservation().getReservationDate())
                        .createdDate(review.getCreatedAt() != null ? review.getCreatedAt().toLocalDate() : LocalDate.now())
                        .revisitIntention(review.isRevisitIntention())
                        .isMyReview(true)
                        .build())
                .collect(Collectors.toList());
    }

    public Long getReviewableReservationId(Long hospitalId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Reservation> allReservations = reservationRepository.findByUserId(user.getId());

        for (Reservation r : allReservations) {
            if (r.getHospital().getId().equals(hospitalId) &&
                    r.getStatus() == ReservationStatus.COMPLETED) {

                if (!reviewRepository.existsByReservationId(r.getId())) {
                    return r.getId();
                }
            }
        }
        throw new IllegalStateException("리뷰를 작성할 수 있는 진료 내역이 없습니다.");
    }

    public ReviewFormResponse getReviewFormInfo(Long reservationId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 예약만 리뷰를 작성할 수 있습니다.");
        }

        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new IllegalStateException("진료가 완료된 예약만 리뷰를 작성할 수 있습니다.");
        }

        return ReviewFormResponse.from(reservation);
    }

    @Transactional
    public Long createReview(ReviewRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        if (reviewRepository.existsByReservationId(reservation.getId())) {
            throw new IllegalStateException("이미 리뷰를 작성한 예약입니다.");
        }

        boolean revisit = "yes".equalsIgnoreCase(request.revisitIntention());
        Department department = Department.valueOf(request.department());

        Review review = new Review(
                reservation,
                department,
                request.content(),
                revisit
        );

        reviewRepository.save(review);
        return review.getId();
    }

    @Transactional
    public void deleteReview(Long reviewId, String username) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 리뷰만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    public List<ReviewResponse> getHospitalReviews(Long hospitalId, String currentUsername) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));

        List<Review> reviews = reviewRepository.findAllByHospitalOrderByCreatedAtDesc(hospital);

        return reviews.stream()
                .map(review -> {
                    boolean isMyReview = currentUsername != null &&
                            review.getUser().getUsername().equals(currentUsername);

                    return ReviewResponse.builder()
                            .reviewId(review.getId())
                            .hospitalId(review.getHospital().getId())
                            .hospitalName(review.getHospital().getName())
                            .hospitalImageUrl(review.getHospital().getImageUrl())
                            .username(maskUsername(review.getUser().getUsername()))
                            .department(review.getDepartment().getDepartment())
                            .animalType(review.getReservation().getAnimalType().getDescription())
                            .breed(review.getReservation().getBreed().getDescription())
                            .content(review.getContent())
                            .visitDate(review.getReservation().getReservationDate())
                            .createdDate(review.getCreatedAt() != null ?
                                    review.getCreatedAt().toLocalDate() : LocalDate.now())
                            .revisitIntention(review.isRevisitIntention())
                            .isMyReview(isMyReview)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getMyHospitalReviews(String hospitalUsername) {
        Hospital hospital = hospitalRepository.findByUsername(hospitalUsername)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));

        List<Review> reviews = reviewRepository.findAllByHospitalOrderByCreatedAtDesc(hospital);

        return reviews.stream()
                .map(review -> ReviewResponse.builder()
                        .reviewId(review.getId())
                        .hospitalId(review.getHospital().getId())
                        .hospitalName(review.getHospital().getName())
                        .hospitalImageUrl(review.getHospital().getImageUrl())
                        .username(review.getUser().getUsername())
                        .department(review.getDepartment().getDepartment())
                        .animalType(review.getReservation().getAnimalType().getDescription())
                        .breed(review.getReservation().getBreed().getDescription())
                        .content(review.getContent())
                        .visitDate(review.getReservation().getReservationDate())
                        .createdDate(review.getCreatedAt() != null ?
                                review.getCreatedAt().toLocalDate() : LocalDate.now())
                        .revisitIntention(review.isRevisitIntention())
                        .isMyReview(false)
                        .build())
                .collect(Collectors.toList());
    }

    public Long getReviewCountByHospitalId(Long hospitalId) {
        return reviewRepository.countByHospitalId(hospitalId);
    }

    private String maskUsername(String username) {
        if (username == null || username.length() <= 1) {
            return "익명";
        }

        if (username.length() == 2) {
            return username.charAt(0) + "*";
        }

        if (username.length() == 3) {
            return username.charAt(0) + "*" + username.charAt(2);
        }

        return username.charAt(0) + "*".repeat(username.length() - 2) + username.charAt(username.length() - 1);
    }
}