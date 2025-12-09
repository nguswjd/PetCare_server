package com.pet.petCare.repository;

import com.pet.petCare.domain.ViewHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {

    @Query("SELECT vh FROM ViewHistory vh WHERE vh.userId = :userId ORDER BY vh.viewedAt DESC")
    List<ViewHistory> findByUserIdOrderByViewedAtDesc(Long userId, Pageable pageable);

    Optional<ViewHistory> findByUserIdAndHospitalId(Long userId, Long hospitalId);
}