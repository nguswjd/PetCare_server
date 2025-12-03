package com.pet.petCare.controller;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.dto.HospitalDetailResponse;
import com.pet.petCare.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class HospitalController {
    private final HospitalService hospitalService;

    @GetMapping("/api/v1/hospital/{id}")
    public HospitalDetailResponse getHospitalDetail(@PathVariable Long id) {
        return hospitalService.getHospital(id);
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