package com.pet.petCare.service;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.ViewHistory;
import com.pet.petCare.dto.ViewHistoryResponse;
import com.pet.petCare.repository.HospitalRepository;
import com.pet.petCare.repository.ViewHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewHistoryService {

    private final ViewHistoryRepository viewHistoryRepository;
    private final HospitalRepository hospitalRepository;

    @Transactional
    public void saveViewHistory(Long userId, Long hospitalId) {
        if (!hospitalRepository.existsById(hospitalId)) {
            log.warn("존재하지 않는 병원 ID 저장 시도: {}", hospitalId);
            return;
        }

        viewHistoryRepository.findByUserIdAndHospitalId(userId, hospitalId)
                .ifPresentOrElse(
                        existing -> existing.setViewedAt(LocalDateTime.now()),
                        () -> {
                            ViewHistory newHistory = ViewHistory.builder()
                                    .userId(userId)
                                    .hospitalId(hospitalId)
                                    .build();
                            viewHistoryRepository.save(newHistory);
                        }
                );
    }

    @Transactional(readOnly = true)
    public List<ViewHistoryResponse> getUserViewHistory(Long userId) {
        List<ViewHistory> histories = viewHistoryRepository
                .findByUserIdOrderByViewedAtDesc(userId, PageRequest.of(0, 50));

        List<ViewHistoryResponse> result = new ArrayList<>();
        Set<Long> uniqueHospitalIds = new HashSet<>();

        for (ViewHistory vh : histories) {
            try {
                Hospital hospital = vh.getHospital();

                if (hospital == null || uniqueHospitalIds.contains(hospital.getId())) {
                    continue;
                }

                uniqueHospitalIds.add(hospital.getId());

                result.add(ViewHistoryResponse.builder()
                        .id(hospital.getId())
                        .name(hospital.getName())
                        .address(hospital.getAddress())
                        .imageUrl(hospital.getImageUrl())
                        .operatingStatus(hospital.getOperatingStatus() != null ? hospital.getOperatingStatus().name() : "UNKNOWN")
                        .visitedAt(vh.getViewedAt().toString())
                        .build());

                if (result.size() >= 10) {
                    break;
                }

            } catch (Exception e) {
                log.error("데이터 변환 중 에러 발생", e);
            }
        }

        return result;
    }
}