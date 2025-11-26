package com.pet.petCare.service;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.dto.*;
import com.pet.petCare.repository.HospitalRepository;
import com.pet.petCare.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@Transactional
@RequiredArgsConstructor
public class HospitalAuthService {

    private final HospitalRepository hospitalRepository;
    private final JwtUtil jwtUtil;

    private String encodePassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 암호화 중 오류", e);
        }
    }

    public HospitalAuthResponse signup(HospitalSignupRequest request) {
        validateSignupRequest(request);

        if (hospitalRepository.existsByUsername(request.username())) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        if (hospitalRepository.existsByHospitalNumber(request.hospitalNumber())) {
            throw new RuntimeException("이미 등록된 사업장번호입니다.");
        }

        if (hospitalRepository.existsByBusinessRegistrationNumber(request.businessRegistrationNumber())) {
            throw new RuntimeException("이미 등록된 사업자등록번호입니다.");
        }

        String encodedPassword = encodePassword(request.password());

        Hospital hospital = new Hospital(
                request.representativeName(),
                request.username(),
                encodedPassword,
                request.name(),
                request.hospitalNumber(),
                request.businessRegistrationNumber(),
                request.address()
        );

        if (request.hasParking() != null) {
            hospital.setHasParking(request.hasParking());
        }
        if (request.departments() != null && !request.departments().isEmpty()) {
            hospital.setDepartments(request.departments());
        }
        if (request.animalTypes() != null && !request.animalTypes().isEmpty()) {
            hospital.setAnimalTypes(request.animalTypes());
        }
        if (request.breeds() != null && !request.breeds().isEmpty()) {
            hospital.setBreeds(request.breeds());
        }
        if (request.holidays() != null && !request.holidays().isEmpty()) {
            hospital.setHolidays(request.holidays());
        }

        if (request.operatingStartTime() != null) {
            hospital.setOperatingStartTime(request.operatingStartTime());
        }
        if (request.operatingEndTime() != null) {
            hospital.setOperatingEndTime(request.operatingEndTime());
        }
        if (request.is24Hours() != null) {
            hospital.setIs24Hours(request.is24Hours());
        }
        if (request.breakTimes() != null && !request.breakTimes().isEmpty()) {
            hospital.setBreakTimes(request.breakTimes());
        }

        if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
            hospital.setImageUrl(request.imageUrl());
        }
        if (request.description() != null && !request.description().isBlank()) {
            hospital.setDescription(request.description());
        }

        hospitalRepository.save(hospital);

        String token = jwtUtil.generateToken(hospital.getUsername());
        return HospitalAuthResponse.success(token, hospital);
    }

    @Transactional
    public HospitalAuthResponse updateDetails(String username, HospitalDetailRequest request) {
        Hospital hospital = hospitalRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다."));

        if (request.hasParking() != null) {
            hospital.setHasParking(request.hasParking());
        }

        if (request.departments() != null) {
            hospital.setDepartments(request.departments());
        }

        if (request.animalTypes() != null) {
            hospital.setAnimalTypes(request.animalTypes());
        }

        if (request.breeds() != null) {
            hospital.setBreeds(request.breeds());
        }

        if (request.holidays() != null) {
            hospital.setHolidays(request.holidays());
        }

        if (request.operatingStartTime() != null) {
            hospital.setOperatingStartTime(request.operatingStartTime());
        }

        if (request.operatingEndTime() != null) {
            hospital.setOperatingEndTime(request.operatingEndTime());
        }

        if (request.is24Hours() != null) {
            hospital.setIs24Hours(request.is24Hours());
        }

        if (request.breakTimes() != null) {
            hospital.setBreakTimes(request.breakTimes());
        }

        if (request.imageUrl() != null) {
            hospital.setImageUrl(request.imageUrl());
        }

        if (request.description() != null) {
            hospital.setDescription(request.description());
        }

        hospitalRepository.save(hospital);

        String token = jwtUtil.generateToken(hospital.getUsername());
        return HospitalAuthResponse.success(token, hospital);
    }

    @Transactional(readOnly = true)
    public HospitalAuthResponse login(HospitalLoginRequest request) {
        validateLoginRequest(request);

        Hospital hospital = hospitalRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다."));

        String encodedPassword = encodePassword(request.password());
        if (!encodedPassword.equals(hospital.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.generateToken(hospital.getUsername());
        return HospitalAuthResponse.success(token, hospital);
    }

    @Transactional(readOnly = true)
    public HospitalAuthResponse getCurrentHospital(String username) {
        Hospital hospital = hospitalRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다."));

        return HospitalAuthResponse.builder()
                .username(hospital.getUsername())
                .representativeName(hospital.getRepresentativeName())
                .name(hospital.getName())
                .hospitalNumber(hospital.getHospitalNumber())
                .businessRegistrationNumber(hospital.getBusinessRegistrationNumber())
                .address(hospital.getAddress())
                .status(hospital.getStatus())
                .hasParking(hospital.isHasParking())
                .departments(hospital.getDepartments())
                .animalTypes(hospital.getAnimalTypes())
                .breeds(hospital.getBreeds())
                .holidays(hospital.getHolidays())

                .operatingStartTime(hospital.getOperatingStartTime())
                .operatingEndTime(hospital.getOperatingEndTime())
                .is24Hours(hospital.isIs24Hours())
                .breakTimes(hospital.getBreakTimes())

                .imageUrl(hospital.getImageUrl())
                .description(hospital.getDescription())
                .build();
    }

    public HospitalAuthResponse logout() {
        return HospitalAuthResponse.success("로그아웃되었습니다.");
    }

    public HospitalAuthResponse withdraw(String username, HospitalWithdrawRequest request) {
        Hospital hospital = hospitalRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다."));

        String encodedPassword = encodePassword(request.password());
        if (!encodedPassword.equals(hospital.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        hospitalRepository.delete(hospital);

        return HospitalAuthResponse.success("회원탈퇴가 완료되었습니다.");
    }

    public boolean isUsernameTaken(String username) {
        validateUsername(username);
        return hospitalRepository.existsByUsername(username);
    }

    public boolean isHospitalNumberTaken(String hospitalNumber) {
        validateHospitalNumber(hospitalNumber);
        return hospitalRepository.existsByHospitalNumber(hospitalNumber);
    }

    public boolean isBusinessRegistrationNumberTaken(String businessRegistrationNumber) {
        validateBusinessRegistrationNumber(businessRegistrationNumber);
        return hospitalRepository.existsByBusinessRegistrationNumber(businessRegistrationNumber);
    }

    private void validateSignupRequest(HospitalSignupRequest request) {
        if (request.representativeName() == null || request.representativeName().isBlank()) {
            throw new RuntimeException("대표자 이름은 필수항목입니다.");
        }
        if (request.username() == null || request.username().isBlank()) {
            throw new RuntimeException("아이디는 필수항목입니다.");
        }
        if (request.username().length() < 4 || request.username().length() > 15) {
            throw new RuntimeException("아이디는 4자 이상 15자 이하여야 합니다.");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new RuntimeException("비밀번호는 필수항목입니다.");
        }
        if (request.password().length() < 6) {
            throw new RuntimeException("비밀번호는 최소 6자 이상이어야 합니다.");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new RuntimeException("병원 이름은 필수항목입니다.");
        }
        if (request.hospitalNumber() == null || request.hospitalNumber().isBlank()) {
            throw new RuntimeException("사업장번호는 필수항목입니다.");
        }
        if (request.businessRegistrationNumber() == null || request.businessRegistrationNumber().isBlank()) {
            throw new RuntimeException("사업자등록번호는 필수항목입니다.");
        }
        if (request.address() == null || request.address().isBlank()) {
            throw new RuntimeException("주소는 필수항목입니다.");
        }
    }

    private void validateLoginRequest(HospitalLoginRequest request) {
        if (request.username() == null || request.username().isBlank()) {
            throw new RuntimeException("아이디는 필수항목입니다.");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new RuntimeException("비밀번호는 필수항목입니다.");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new RuntimeException("아이디를 입력해주세요.");
        }
        if (username.length() < 4 || username.length() > 15) {
            throw new RuntimeException("아이디는 4자 이상 15자 이하여야 합니다.");
        }
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            throw new RuntimeException("아이디는 영문자와 숫자만 사용 가능합니다.");
        }
    }

    private void validateHospitalNumber(String hospitalNumber) {
        if (hospitalNumber == null || hospitalNumber.isBlank()) {
            throw new RuntimeException("사업장번호를 입력해주세요.");
        }
    }

    private void validateBusinessRegistrationNumber(String businessRegistrationNumber) {
        if (businessRegistrationNumber == null || businessRegistrationNumber.isBlank()) {
            throw new RuntimeException("사업자등록번호를 입력해주세요.");
        }
        if (!businessRegistrationNumber.matches("^\\d{3}-?\\d{2}-?\\d{5}$")) {
            throw new RuntimeException("올바른 사업자등록번호 형식이 아닙니다. (예: 123-45-67890)");
        }
    }
}