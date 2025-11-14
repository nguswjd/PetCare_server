package com.pet.petCare.controller;

import com.pet.petCare.dto.HospitalDetailResponse;
import com.pet.petCare.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class HospitalController {
    private final HospitalService hospitalService;

    @GetMapping("/api/v1/hospital/{id}")
    public HospitalDetailResponse getHospitalDetail(@PathVariable Long id) {
        return hospitalService.getHospital(id);
    }
}
