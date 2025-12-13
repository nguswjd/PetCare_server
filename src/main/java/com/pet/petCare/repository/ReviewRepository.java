package com.pet.petCare.repository;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.Review;
import com.pet.petCare.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReservationId(Long reservationId);
    List<Review> findAllByUserOrderByCreatedAtDesc(User user);
    List<Review> findAllByHospitalOrderByCreatedAtDesc(Hospital hospital);
}