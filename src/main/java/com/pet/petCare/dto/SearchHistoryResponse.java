package com.pet.petCare.dto;

import com.pet.petCare.domain.SearchHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SearchHistoryResponse {

    private final Long id;
    private final String keyword;
    private final LocalDateTime searchedAt;

    public SearchHistoryResponse(SearchHistory history) {
        this.id = history.getId();
        this.keyword = history.getKeyword();
        this.searchedAt = history.getSearchedAt();
    }
}