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
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/v1/auth/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        try {
            AuthResponse response = authService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/api/v1/auth/animal-types")
    public AnimalTypeResponse getAnimalTypes() {
        return AnimalTypeResponse.all();
    }

    @GetMapping("/api/v1/auth/breeds/{animalType}")
    public ResponseEntity<?> getBreedsByAnimalType(@PathVariable String animalType) {
        try {
            AnimalType type = AnimalType.valueOf(animalType.toUpperCase());
            return ResponseEntity.ok(BreedFilterResponse.from(type));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.error("유효하지 않은 동물 종류입니다"));
        }
    }
}