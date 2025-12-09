package com.pet.petCare.controller;

import com.pet.petCare.dto.SearchHistoryResponse;
import com.pet.petCare.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search-history")
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @GetMapping("/popular/keywords")
    public ResponseEntity<List<String>> getPopularKeywords(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(searchHistoryService.getPopularKeywords(limit));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<SearchHistoryResponse>> getUserHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(searchHistoryService.getUserSearchHistory(userId, page, size));
    }

    @DeleteMapping("/{historyId}")
    public ResponseEntity<String> deleteHistory(@PathVariable Long historyId) {
        searchHistoryService.deleteHistory(historyId);
        return ResponseEntity.ok("검색 기록이 삭제되었습니다.");
    }

    @DeleteMapping("/user/{userId}/keyword/{keyword}")
    public ResponseEntity<Void> deleteSearchHistoryByKeyword(
            @PathVariable Long userId,
            @PathVariable String keyword
    ) {
        searchHistoryService.deleteByUserIdAndKeyword(userId, keyword);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}/all")
    public ResponseEntity<Void> deleteAllSearchHistory(@PathVariable Long userId) {
        searchHistoryService.deleteAllByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}