package com.pet.petCare.repository;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.Review;
import com.pet.petCare.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReservationId(Long reservationId);
    List<Review> findAllByUserOrderByCreatedAtDesc(User user);
    List<Review> findAllByHospitalOrderByCreatedAtDesc(Hospital hospital);

    void deleteByUserId(Long userId);

    void deleteByHospitalId(Long hospitalId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.hospital.id = :hospitalId")
    Long countByHospitalId(@Param("hospitalId") Long hospitalId);

    @Query("SELECT r.hospital.id, COUNT(r) FROM Review r GROUP BY r.hospital.id")
    List<Object[]> countReviewsByHospital();
}