package com.pet.petCare.controller;

import com.pet.petCare.dto.*;
import com.pet.petCare.security.JwtUtil;
import com.pet.petCare.service.HospitalAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hospital/auth")
public class HospitalAuthController {

    private final HospitalAuthService hospitalAuthService;
    private final JwtUtil jwtUtil;

    @PostMapping(value = "/signup", consumes = {"multipart/form-data"})
    public ResponseEntity<HospitalAuthResponse> signup(
            @RequestPart("hospital") HospitalSignupRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            HospitalAuthResponse response = hospitalAuthService.signup(request, imageFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(HospitalAuthResponse.error(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(HospitalAuthResponse.error("이미지 업로드 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<HospitalAuthResponse> login(@RequestBody HospitalLoginRequest request) {
        try {
            HospitalAuthResponse response = hospitalAuthService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(HospitalAuthResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<HospitalAuthResponse> getCurrentHospital(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username == null || !jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(HospitalAuthResponse.error("유효하지 않은 토큰입니다."));
            }

            HospitalAuthResponse response = hospitalAuthService.getCurrentHospital(username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(HospitalAuthResponse.error("인증에 실패했습니다."));
        }
    }

    @PatchMapping("/details")
    public ResponseEntity<HospitalAuthResponse> updateDetails(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody HospitalDetailRequest request) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username == null || !jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(HospitalAuthResponse.error("유효하지 않은 토큰입니다."));
            }

            HospitalAuthResponse response = hospitalAuthService.updateDetails(username, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(HospitalAuthResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<HospitalAuthResponse> checkUsername(@RequestParam String username) {
        try {
            if (hospitalAuthService.isUsernameTaken(username)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(HospitalAuthResponse.error("이미 사용 중인 아이디입니다."));
            } else {
                return ResponseEntity.ok(HospitalAuthResponse.success("사용 가능한 아이디입니다."));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(HospitalAuthResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/check-hospital-number")
    public ResponseEntity<HospitalAuthResponse> checkHospitalNumber(@RequestParam String hospitalNumber) {
        try {
            if (hospitalAuthService.isHospitalNumberTaken(hospitalNumber)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(HospitalAuthResponse.error("이미 등록된 사업장번호입니다."));
            } else {
                return ResponseEntity.ok(HospitalAuthResponse.success("사용 가능한 사업장번호입니다."));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(HospitalAuthResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/check-business-number")
    public ResponseEntity<HospitalAuthResponse> checkBusinessNumber(
            @RequestParam String businessRegistrationNumber) {
        try {
            if (hospitalAuthService.isBusinessRegistrationNumberTaken(businessRegistrationNumber)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(HospitalAuthResponse.error("이미 등록된 사업자등록번호입니다."));
            } else {
                return ResponseEntity.ok(HospitalAuthResponse.success("사용 가능한 사업자등록번호입니다."));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(HospitalAuthResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<HospitalAuthResponse> logout(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username == null || !jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(HospitalAuthResponse.error("유효하지 않은 토큰입니다."));
            }

            HospitalAuthResponse response = hospitalAuthService.logout();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(HospitalAuthResponse.error("로그아웃 중 오류가 발생했습니다."));
        }
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<HospitalAuthResponse> withdraw(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody HospitalWithdrawRequest request) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username == null || !jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(HospitalAuthResponse.error("유효하지 않은 토큰입니다."));
            }

            HospitalAuthResponse response = hospitalAuthService.withdraw(username, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(HospitalAuthResponse.error(e.getMessage()));
        }
    }
}