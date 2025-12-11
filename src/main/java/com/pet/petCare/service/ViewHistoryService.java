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
            throw new IllegalArgumentException("존재하지 않는 병원입니다: " + hospitalId);
        }

        ViewHistory history = viewHistoryRepository
                .findByUserIdAndHospitalId(userId, hospitalId)
                .orElseGet(() -> ViewHistory.builder()
                        .userId(userId)
                        .hospitalId(hospitalId)
                        .build());

        history.setViewedAt(LocalDateTime.now());
        viewHistoryRepository.save(history);

        log.debug("병원 조회 기록 저장 완료 - userId: {}, hospitalId: {}", userId, hospitalId);
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

                if (hospital == null) {
                    log.warn("ViewHistory ID {}에 연결된 병원이 없습니다", vh.getId());
                    continue;
                }

                if (uniqueHospitalIds.contains(hospital.getId())) {
                    continue;
                }

                uniqueHospitalIds.add(hospital.getId());

                result.add(ViewHistoryResponse.builder()
                        .id(hospital.getId())
                        .name(hospital.getName())
                        .address(hospital.getAddress())
                        .imageUrl(hospital.getImageUrl())
                        .operatingStatus(hospital.getOperatingStatus() != null
                                ? hospital.getOperatingStatus().name()
                                : "UNKNOWN")
                        .visitedAt(vh.getViewedAt().toString())
                        .build());

                if (result.size() >= 10) {
                    break;
                }

            } catch (Exception e) {
                log.error("ViewHistory ID {} 데이터 변환 중 에러 발생", vh.getId(), e);
            }
        }

        log.debug("사용자 {} 조회 기록 조회 완료: {} 건", userId, result.size());
        return result;
    }
}