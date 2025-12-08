package com.pet.petCare.repository;

import com.pet.petCare.domain.SearchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    Page<SearchHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT h.keyword FROM SearchHistory h GROUP BY h.keyword ORDER BY COUNT(h.keyword) DESC")
    List<String> findPopularKeywords(Pageable pageable);
}
