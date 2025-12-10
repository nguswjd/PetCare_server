package com.pet.petCare.repository;

import com.pet.petCare.domain.SearchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query("SELECT sh.keyword FROM SearchHistory sh " +
            "GROUP BY sh.keyword " +
            "ORDER BY COUNT(sh.keyword) DESC")
    List<String> findPopularKeywords(Pageable pageable);

    Page<SearchHistory> findByUserId(Long userId, Pageable pageable);

    Optional<SearchHistory> findByUserIdAndKeyword(Long userId, String keyword);

    @Transactional
    void deleteByUserIdAndKeyword(Long userId, String keyword);

    @Transactional
    void deleteByUserId(Long userId);
}