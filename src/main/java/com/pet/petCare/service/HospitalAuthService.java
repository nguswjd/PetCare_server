package com.pet.petCare.service;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.dto.*;
import com.pet.petCare.repository.HospitalRepository;
import com.pet.petCare.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class HospitalAuthService {

    private final HospitalRepository hospitalRepository;
    private final S3Service s3Service;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public HospitalAuthResponse signup(HospitalSignupRequest request, MultipartFile imageFile) throws IOException {
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

        String encodedPassword = passwordEncoder.encode(request.password());

        Hospital hospital = new Hospital(
                request.representativeName(),
                request.username(),
                encodedPassword,
                request.name(),
                request.hospitalNumber(),
                request.businessRegistrationNumber(),
                request.address()
        );

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = s3Service.uploadImage(imageFile, "hospitals");
            hospital.setImageUrl(imageUrl);
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

        if (!passwordEncoder.matches(request.password(), hospital.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.generateToken(hospital.getUsername());
        return HospitalAuthResponse.success(token, hospital);
    }

    @Transactional
    public HospitalAuthResponse updateDetails(String username, HospitalDetailRequest request) {
        Hospital hospital = hospitalRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다."));

        if (request.operatingStartTime() != null) {
            hospital.setOperatingStartTime(request.operatingStartTime());
        }

        if (request.operatingEndTime() != null) {
            hospital.setOperatingEndTime(request.operatingEndTime());
        }

        if (request.is24Hours() != null) {
            hospital.setIs24Hours(request.is24Hours());
        }

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

        if (request.breakTimes() != null) {
            hospital.setBreakTimes(request.breakTimes());
        }

        if (request.description() != null) {
            hospital.setDescription(request.description());
        }

        hospitalRepository.save(hospital);

        String token = jwtUtil.generateToken(hospital.getUsername());
        return HospitalAuthResponse.success(token, hospital);
    }

    private void validateSignupRequest(HospitalSignupRequest request) {
        if (request.representativeName() == null || request.representativeName().isBlank()) {
            throw new RuntimeException("대표자명은 필수항목입니다.");
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
            throw new RuntimeException("병원명은 필수항목입니다.");
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

    public boolean isUsernameTaken(String username) {
        validateUsername(username);
        return hospitalRepository.existsByUsername(username);
    }

    public boolean isHospitalNumberTaken(String hospitalNumber) {
        if (hospitalNumber == null || hospitalNumber.isBlank()) {
            throw new RuntimeException("사업장번호를 입력해주세요.");
        }
        return hospitalRepository.existsByHospitalNumber(hospitalNumber);
    }

    public boolean isBusinessRegistrationNumberTaken(String businessRegistrationNumber) {
        if (businessRegistrationNumber == null || businessRegistrationNumber.isBlank()) {
            throw new RuntimeException("사업자등록번호를 입력해주세요.");
        }
        return hospitalRepository.existsByBusinessRegistrationNumber(businessRegistrationNumber);
    }

    public HospitalAuthResponse logout() {
        return HospitalAuthResponse.success("로그아웃되었습니다.");
    }

    public HospitalAuthResponse withdraw(String username, HospitalWithdrawRequest request) {
        Hospital hospital = hospitalRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.password(), hospital.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        if (hospital.getImageUrl() != null) {
            s3Service.deleteImage(hospital.getImageUrl());
        }

        hospitalRepository.delete(hospital);

        return HospitalAuthResponse.success("회원탈퇴가 완료되었습니다.");
    }

    @Transactional(readOnly = true)
    public HospitalAuthResponse getCurrentHospital(String username) {
        Hospital hospital = hospitalRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다."));

        return HospitalAuthResponse.builder()
                .username(hospital.getUsername())
                .name(hospital.getName())
                .representativeName(hospital.getRepresentativeName())
                .hospitalNumber(hospital.getHospitalNumber())
                .businessRegistrationNumber(hospital.getBusinessRegistrationNumber())
                .address(hospital.getAddress())
                .imageUrl(hospital.getImageUrl())
                .build();
    }
}