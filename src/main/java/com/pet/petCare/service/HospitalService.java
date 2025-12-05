package com.pet.petCare.service;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.dto.HospitalDetailResponse;
import com.pet.petCare.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HospitalService {
    private final HospitalRepository hospitalRepository;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;

    public HospitalDetailResponse getHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId).orElseThrow();
        return HospitalDetailResponse.from(hospital);
    }

    // 검색 메서드 추가
    public List<Hospital> searchHospitals(String keyword) {
        return hospitalRepository.findByNameContainingOrAddressContaining(keyword, keyword);
    }

    public Hospital registerHospital(Hospital hospital, MultipartFile imageFile) throws IOException {
        hospital.setPassword(passwordEncoder.encode(hospital.getPassword()));

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = s3Service.uploadImage(imageFile, "hospitals");
            hospital.setImageUrl(imageUrl);
        }

        return hospitalRepository.save(hospital);
    }

    public Hospital updateHospital(Long hospitalId, Hospital updatedInfo, MultipartFile imageFile) throws IOException {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));

        hospital.setName(updatedInfo.getName());
        hospital.setAddress(updatedInfo.getAddress());
        hospital.setDescription(updatedInfo.getDescription());

        if (imageFile != null && !imageFile.isEmpty()) {
            if (hospital.getImageUrl() != null) {
                s3Service.deleteImage(hospital.getImageUrl());
            }
            String imageUrl = s3Service.uploadImage(imageFile, "hospitals");
            hospital.setImageUrl(imageUrl);
        }

        return hospitalRepository.save(hospital);
    }

    public void deleteHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));

        if (hospital.getImageUrl() != null) {
            s3Service.deleteImage(hospital.getImageUrl());
        }

        hospitalRepository.delete(hospital);
    }
}