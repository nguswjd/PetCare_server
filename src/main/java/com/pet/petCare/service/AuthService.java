package com.pet.petCare.service;

import com.pet.petCare.domain.User;
import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.domain.enums.Breed;
import com.pet.petCare.dto.AuthResponse;
import com.pet.petCare.dto.LoginRequest;
import com.pet.petCare.dto.SignupRequest;
import com.pet.petCare.dto.UpdateUserRequest;
import com.pet.petCare.dto.WithdrawRequest;
import com.pet.petCare.repository.UserRepository;
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
public class AuthService {

    private final UserRepository userRepository;
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

    // 회원가입
    public AuthResponse signup(SignupRequest request) {
        validateSignupRequest(request);

        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        String encodedPassword = encodePassword(request.password());

        User user = new User(
                request.name(),
                request.username(),
                encodedPassword,
                request.phoneNumber()
        );

        user.setSpecies(request.species());
        user.setBreed(request.breed());
        user.setMarketingConsent(request.marketingConsent());

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return AuthResponse.success(token, user);
    }

    // 로그인
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        validateLoginRequest(request);

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String encodedPassword = encodePassword(request.password());
        if (!encodedPassword.equals(user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return AuthResponse.success(token, user);
    }

    // 내 정보 수정
    @Transactional
    public AuthResponse updateUser(String username, UpdateUserRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            if (!request.getPhoneNumber().equals(user.getPhoneNumber()) && isPhoneTaken(request.getPhoneNumber())) {
                throw new RuntimeException("이미 등록된 번호입니다.");
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getSpecies() != null && !request.getSpecies().isBlank()) {
            user.setSpecies(AnimalType.valueOf(request.getSpecies().toUpperCase()));
            user.setBreed(null);
        }

        if (request.getBreed() != null && !request.getBreed().isBlank()) {
            user.setBreed(Breed.valueOf(request.getBreed().toUpperCase()));
        }

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return AuthResponse.success(token, user);
    }

    // 회원가입 검증
    private void validateSignupRequest(SignupRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new RuntimeException("필수항목 입니다.");
        }
        if (request.username() == null || request.username().isBlank()) {
            throw new RuntimeException("필수항목 입니다.");
        }
        if (request.username().length() < 4 || request.username().length() > 15) {
            throw new RuntimeException("아이디는 4자 이상 15자 이하여야 합니다.");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new RuntimeException("필수항목 입니다.");
        }
        if (request.password().length() < 6) {
            throw new RuntimeException("비밀번호는 최소 6자 이상이어야 합니다.");
        }
        if (request.phoneNumber() == null || request.phoneNumber().isBlank()) {
            throw new RuntimeException("필수항목 입니다.");
        }
        if (!request.phoneNumber().matches("^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$")) {
            throw new RuntimeException("올바른 휴대폰 번호 형식이 아닙니다.");
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

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new RuntimeException("휴대폰 번호를 입력해주세요.");
        }
        if (!phoneNumber.matches("^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$")) {
            throw new RuntimeException("올바른 휴대폰 번호 형식이 아닙니다.");
        }
    }

    // 로그인 검증
    private void validateLoginRequest(LoginRequest request) {
        if (request.username() == null || request.username().isBlank()) {
            throw new RuntimeException("필수항목 입니다.");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new RuntimeException("필수항목 입니다.");
        }
    }

    public boolean isUsernameTaken(String username) {
        validateUsername(username);
        return userRepository.existsByUsername(username);
    }

    public boolean isPhoneTaken(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    // 로그아웃
    public AuthResponse logout() {
        return AuthResponse.success("로그아웃되었습니다.");
    }

    // 회원탈퇴
    public AuthResponse withdraw(String username, WithdrawRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String encodedPassword = encodePassword(request.password());
        if (!encodedPassword.equals(user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        userRepository.delete(user);

        return AuthResponse.success("회원탈퇴가 완료되었습니다.");
    }

    public AuthResponse withdrawHardDelete(String username, WithdrawRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String encodedPassword = encodePassword(request.password());
        if (!encodedPassword.equals(user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        userRepository.delete(user);
        return AuthResponse.success("회원탈퇴가 완료되었습니다.");
    }

    @Transactional(readOnly = true)
    public AuthResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return AuthResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .species(user.getSpecies())
                .breed(user.getBreed())
                .marketingConsent(user.getMarketingConsent())
                .build();
    }
}
