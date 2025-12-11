package com.pet.petCare.controller;

import com.pet.petCare.dto.ViewHistoryRequest;
import com.pet.petCare.dto.ViewHistoryResponse;
import com.pet.petCare.security.JwtUtil;
import com.pet.petCare.service.HospitalService;
import com.pet.petCare.service.ViewHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/view-history")
public class ViewHistoryController {

    private final ViewHistoryService viewHistoryService;
    private final HospitalService hospitalService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<Void> saveViewHistory(
            @RequestBody ViewHistoryRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            Long userId = hospitalService.getUserIdByUsername(username);

            viewHistoryService.saveViewHistory(userId, request.getHospitalId());
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            log.error("Invalid hospital ID: ", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to save view history: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<ViewHistoryResponse>> getUserViewHistory(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            Long userId = hospitalService.getUserIdByUsername(username);

            List<ViewHistoryResponse> history = viewHistoryService.getUserViewHistory(userId);
            return ResponseEntity.ok(history);

        } catch (Exception e) {
            log.error("Failed to get view history: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}