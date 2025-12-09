package com.pet.petCare.controller;

import com.pet.petCare.dto.ViewHistoryRequest;
import com.pet.petCare.dto.ViewHistoryResponse;
import com.pet.petCare.security.JwtUtil;
import com.pet.petCare.service.HospitalService;
import com.pet.petCare.service.ViewHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        Long userId = hospitalService.getUserIdByUsername(username);

        viewHistoryService.saveViewHistory(userId, request.getHospitalId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<ViewHistoryResponse>> getUserViewHistory(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        Long userId = hospitalService.getUserIdByUsername(username);

        List<ViewHistoryResponse> history = viewHistoryService.getUserViewHistory(userId);
        return ResponseEntity.ok(history);
    }
}