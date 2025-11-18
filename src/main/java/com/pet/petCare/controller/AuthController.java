package com.pet.petCare.controller;

import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.dto.*;
import com.pet.petCare.security.JwtUtil;
import com.pet.petCare.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        try {
            AuthResponse response = authService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);

            String username = jwtUtil.extractUsername(token);

            if (username == null || !jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(AuthResponse.error("유효하지 않은 토큰입니다."));
            }
            AuthResponse response = authService.getCurrentUser(username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.error("인증에 실패했습니다."));
        }
    }

    @PatchMapping("/me")
    public ResponseEntity<AuthResponse> updateUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateUserRequest request) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username == null || !jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(AuthResponse.error("유효하지 않은 토큰입니다."));
            }

            AuthResponse response = authService.updateUser(username, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.error(e.getMessage()));
        }
    }


    @GetMapping("/animal-types")
    public AnimalTypeResponse getAnimalTypes() {
        return AnimalTypeResponse.all();
    }

    @GetMapping("/breeds/{animalType}")
    public ResponseEntity<?> getBreedsByAnimalType(@PathVariable String animalType) {
        try {
            AnimalType type = AnimalType.valueOf(animalType.toUpperCase());
            return ResponseEntity.ok(BreedFilterResponse.from(type));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.error("유효하지 않은 동물 종류입니다"));
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<AuthResponse> checkUsername(@RequestParam String username) {
        try {
            if (authService.isUsernameTaken(username)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(AuthResponse.error("이미 사용 중인 아이디입니다."));
            } else {
                return ResponseEntity.ok(AuthResponse.success("사용 가능한 아이디입니다."));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/check-phone")
    public ResponseEntity<AuthResponse> checkPhone(@RequestParam String phone) {
        try {
            if (authService.isPhoneTaken(phone)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(AuthResponse.error("이미 등록된 번호입니다."));
            } else {
                return ResponseEntity.ok(AuthResponse.success("사용 가능한 번호입니다."));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);

            String username = jwtUtil.extractUsername(token);
            if (username == null || !jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(AuthResponse.error("유효하지 않은 토큰입니다."));
            }

            AuthResponse response = authService.logout();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.error("로그아웃 중 오류가 발생했습니다."));
        }
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<AuthResponse> withdraw(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody WithdrawRequest request) {
        try {
            String token = authHeader.substring(7);

            String username = jwtUtil.extractUsername(token);
            if (username == null || !jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(AuthResponse.error("유효하지 않은 토큰입니다."));
            }

            AuthResponse response = authService.withdraw(username, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.error(e.getMessage()));
        }
    }
}