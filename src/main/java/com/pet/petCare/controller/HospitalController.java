package com.pet.petCare.controller;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.dto.HospitalDetailResponse;
import com.pet.petCare.service.HospitalService;
import com.pet.petCare.service.SearchHistoryService;
import com.pet.petCare.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class HospitalController {
    private final HospitalService hospitalService;
    private final SearchHistoryService searchHistoryService;
    private final JwtUtil jwtUtil;

    @GetMapping("/api/v1/hospital/{id}")
    public HospitalDetailResponse getHospitalDetail(@PathVariable Long id) {
        return hospitalService.getHospital(id);
    }

    @GetMapping("/api/v1/hospitals/search")
    public ResponseEntity<List<Hospital>> searchHospitals(
            @RequestParam String keyword,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        System.out.println("===== 검색 시작 =====");
        System.out.println("키워드: " + keyword);
        System.out.println("헤더: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            System.out.println("✅ Bearer 토큰 발견");
            try {
                String token = authHeader.substring(7);
                System.out.println("토큰 추출: " + token.substring(0, 20) + "...");

                String username = jwtUtil.extractUsername(token);
                System.out.println("Username 추출: " + username);

                if (username != null && jwtUtil.validateToken(token, username)) {
                    System.out.println("✅ 토큰 유효함");

                    Long userId = hospitalService.getUserIdByUsername(username);
                    System.out.println("UserId: " + userId);

                    if (userId != null) {
                        System.out.println("✅ 검색 기록 저장 시도");
                        searchHistoryService.saveSearchKeyword(userId, keyword);
                        System.out.println("✅ 검색 기록 저장 완료");
                    } else {
                        System.out.println("❌ userId가 null입니다");
                    }
                } else {
                    System.out.println("❌ 토큰 유효하지 않음");
                }
            } catch (Exception e) {
                System.err.println("검색 기록 저장 실패: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ Authorization 헤더 없음 또는 Bearer 아님");
        }

        List<Hospital> hospitals = hospitalService.searchHospitals(keyword);
        return ResponseEntity.ok(hospitals);
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
}