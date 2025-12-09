package com.pet.petCare.service;

import com.pet.petCare.domain.SearchHistory;
import com.pet.petCare.domain.User;
import com.pet.petCare.dto.SearchHistoryResponse;
import com.pet.petCare.repository.SearchHistoryRepository;
import com.pet.petCare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveSearchKeyword(Long userId, String keyword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        SearchHistory searchHistory = SearchHistory.builder()
                .userId(userId)
                .keyword(keyword)
                .createdAt(LocalDateTime.now())
                .build();

        searchHistoryRepository.save(searchHistory);
    }

    public List<String> getPopularKeywords(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return searchHistoryRepository.findPopularKeywords(pageable);
    }

    public Page<SearchHistoryResponse> getUserSearchHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SearchHistory> histories = searchHistoryRepository.findByUserId(userId, pageable);

        return histories.map(history -> SearchHistoryResponse.builder()
                .id(history.getId())
                .keyword(history.getKeyword())
                .createdAt(history.getCreatedAt())
                .build());
    }

    @Transactional
    public void deleteHistory(Long historyId) {
        searchHistoryRepository.deleteById(historyId);
    }

    @Transactional
    public void deleteByUserIdAndKeyword(Long userId, String keyword) {
        searchHistoryRepository.deleteByUserIdAndKeyword(userId, keyword);
    }

    @Transactional
    public void deleteAllByUserId(Long userId) {
        searchHistoryRepository.deleteByUserId(userId);
    }
}