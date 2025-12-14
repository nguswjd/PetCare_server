package com.pet.petCare.service;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.enums.Breed;
import com.pet.petCare.domain.enums.Department;
import com.pet.petCare.dto.HospitalDetailResponse;
import com.pet.petCare.dto.HospitalSummaryResponse;
import com.pet.petCare.repository.HospitalRepository;
import com.pet.petCare.repository.ReviewRepository;
import com.pet.petCare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class HospitalService {
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;

    public HospitalDetailResponse getHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));
        Long reviewCount = reviewRepository.countByHospitalId(hospitalId);
        return HospitalDetailResponse.from(hospital, reviewCount);
    }

    @Transactional(readOnly = true)
    public List<Hospital> searchHospitals(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String searchKeyword = keyword.trim();

        List<Breed> matchingBreeds = Arrays.stream(Breed.values())
                .filter(breed -> breed.getDescription().contains(searchKeyword))
                .collect(Collectors.toList());

        List<Department> matchingDepartments = Arrays.stream(Department.values())
                .filter(dept -> dept.getDepartment().contains(searchKeyword))
                .collect(Collectors.toList());

        if (matchingBreeds.isEmpty()) matchingBreeds.add(null);
        if (matchingDepartments.isEmpty()) matchingDepartments.add(null);

        List<Hospital> hospitals = hospitalRepository.searchHospitals(searchKeyword, matchingBreeds, matchingDepartments);

        hospitals.forEach(hospital -> {
            hospital.getDepartments().size();
            hospital.getAnimalTypes().size();
            hospital.getBreeds().size();
        });

        return hospitals;
    }

    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> user.getId())
                .orElse(null);
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

    @Transactional(readOnly = true)
    public List<HospitalSummaryResponse> getTopHospitalsByReviewCount(int limit) {
        List<Object[]> reviewCounts = reviewRepository.countReviewsByHospital();

        Map<Long, Long> reviewCountMap = reviewCounts.stream()
                .collect(Collectors.toMap(
                        obj -> (Long) obj[0],
                        obj -> (Long) obj[1]
                ));

        List<Long> topHospitalIds = reviewCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (topHospitalIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Hospital> hospitals = hospitalRepository.findAllByIdIn(topHospitalIds);

        Map<Long, Hospital> hospitalMap = hospitals.stream()
                .collect(Collectors.toMap(Hospital::getId, h -> h));

        return topHospitalIds.stream()
                .filter(hospitalMap::containsKey)
                .map(id -> HospitalSummaryResponse.from(
                        hospitalMap.get(id),
                        reviewCountMap.get(id)
                ))
                .collect(Collectors.toList());
    }
}