package com.pet.petCare.controller;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.dto.HospitalDetailResponse;
import com.pet.petCare.dto.HospitalSummaryResponse;
import com.pet.petCare.dto.RecommendedHospitalResponse;
import com.pet.petCare.service.HospitalService;
import com.pet.petCare.service.SearchHistoryService;
import com.pet.petCare.service.HospitalRecommendationService;
import com.pet.petCare.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HospitalController {
    private final HospitalService hospitalService;
    private final SearchHistoryService searchHistoryService;
    private final HospitalRecommendationService hospitalRecommendationService;
    private final JwtUtil jwtUtil;

    @GetMapping("/api/v1/hospital/{id}")
    public HospitalDetailResponse getHospitalDetail(@PathVariable Long id) {
        return hospitalService.getHospital(id);
    }

    @GetMapping("/api/v1/hospitals/search")
    public ResponseEntity<List<Hospital>> searchHospitals(
            @RequestParam String keyword,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        saveSearchHistoryIfLoggedIn(authHeader, keyword);

        List<Hospital> hospitals = hospitalService.searchHospitals(keyword);
        return ResponseEntity.ok(hospitals);
    }

    @GetMapping("/api/v1/hospitals/top")
    public ResponseEntity<List<HospitalSummaryResponse>> getTopHospitals(
            @RequestParam(defaultValue = "10") int limit) {
        List<HospitalSummaryResponse> topHospitals = hospitalService.getTopHospitalsByReviewCount(limit);
        return ResponseEntity.ok(topHospitals);
    }

    @GetMapping("/api/v1/hospitals/recommended")
    public ResponseEntity<?> getRecommendedHospital() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || authentication.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인이 필요한 서비스입니다.");
            }

            String username = authentication.getName();

            RecommendedHospitalResponse recommendation =
                    hospitalRecommendationService.recommendHospital(username);

            return ResponseEntity.ok(recommendation);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("병원 추천 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("병원 추천 중 오류가 발생했습니다.");
        }
    }

    @PostMapping(value = "/api/v1/hospitals", consumes = {"multipart/form-data"})
    public ResponseEntity<?> registerHospital(
            @RequestPart("hospital") Hospital hospital,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            Hospital savedHospital = hospitalService.registerHospital(hospital, imageFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedHospital);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PutMapping(value = "/api/v1/hospitals/{hospitalId}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateHospital(
            @PathVariable Long hospitalId,
            @RequestPart("hospital") Hospital hospital,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            Hospital updatedHospital = hospitalService.updateHospital(hospitalId, hospital, imageFile);
            return ResponseEntity.ok(updatedHospital);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/v1/hospitals/{hospitalId}")
    public ResponseEntity<?> deleteHospital(@PathVariable Long hospitalId) {
        try {
            hospitalService.deleteHospital(hospitalId);
            return ResponseEntity.ok("병원이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private void saveSearchHistoryIfLoggedIn(String authHeader, String keyword) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username != null && jwtUtil.validateToken(token, username)) {
                Long userId = hospitalService.getUserIdByUsername(username);
                if (userId != null) {
                    searchHistoryService.saveSearchKeyword(userId, keyword);
                }
            }
        } catch (Exception e) {
            log.error("검색 기록 저장 실패", e);
        }
    }
}