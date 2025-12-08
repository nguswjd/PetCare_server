package com.pet.petCare.service;

import com.pet.petCare.domain.SearchHistory;
import com.pet.petCare.dto.SearchHistoryResponse;
import com.pet.petCare.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository repository;

    public List<String> getPopularKeywords(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return repository.findPopularKeywords(pageable);
    }

    public Page<SearchHistoryResponse> getUserSearchHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SearchHistory> result = repository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return result.map(SearchHistoryResponse::new);
    }

    public void deleteHistory(Long historyId) {
        if (!repository.existsById(historyId)) {
            throw new IllegalArgumentException("해당 검색 기록을 찾을 수 없습니다.");
        }
        repository.deleteById(historyId);
    }

    public void saveSearchKeyword(Long userId, String keyword) {
        SearchHistory history = SearchHistory.builder()
                .userId(userId)
                .keyword(keyword)
                .build();
        repository.save(history);
    }
}
