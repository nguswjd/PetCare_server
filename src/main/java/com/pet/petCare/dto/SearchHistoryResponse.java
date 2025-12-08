package com.pet.petCare.dto;

import com.pet.petCare.domain.SearchHistory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SearchHistoryResponse {

    private final Long id;
    private final String keyword;
    private final LocalDateTime createdAt;

    public SearchHistoryResponse(SearchHistory history) {
        this.id = history.getId();
        this.keyword = history.getKeyword();
        this.createdAt = history.getCreatedAt();
    }
}
