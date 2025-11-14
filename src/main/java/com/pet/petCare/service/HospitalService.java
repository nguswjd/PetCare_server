package com.pet.petCare.service;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.dto.HospitalDetailResponse;
import com.pet.petCare.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HospitalService {
    private final HospitalRepository hospitalRepository;

    public HospitalDetailResponse getHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId).orElseThrow();
        return HospitalDetailResponse.from(hospital);
    }
}
