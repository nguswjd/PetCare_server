package com.pet.petCare.controller;

import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.dto.AnimalTypeResponse;
import com.pet.petCare.dto.AuthResponse;
import com.pet.petCare.dto.BreedFilterResponse;
import com.pet.petCare.dto.LoginRequest;
import com.pet.petCare.dto.SignupRequest;
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
        if (authService.isUsernameTaken(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(AuthResponse.error("이미 사용 중인 아이디입니다."));
        } else {
            return ResponseEntity.ok(AuthResponse.success("사용 가능한 아이디입니다.", null));
        }
    }
}
