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
@RequestMapping("/api/v1/hospital/auth")
@RequiredArgsConstructor
public class HospitalAuthController {

    private final HospitalAuthService hospitalAuthService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @ModelAttribute HospitalSignupRequest request,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        System.out.println("=== Hospital Signup 요청 들어옴 ===");
        System.out.println("representativeName: " + request.representativeName());
        System.out.println("username: " + request.username());
        System.out.println("name: " + request.name());
        System.out.println("hospitalNumber: " + request.hospitalNumber());
        System.out.println("businessRegistrationNumber: " + request.businessRegistrationNumber());
        System.out.println("address: " + request.address());
        System.out.println("hasParking: " + request.hasParking());
        System.out.println("departments: " + request.departments());
        System.out.println("animalTypes: " + request.animalTypes());
        System.out.println("breeds: " + request.breeds());
        System.out.println("holidays: " + request.holidays());
        System.out.println("operatingStartTime: " + request.operatingStartTime());
        System.out.println("operatingEndTime: " + request.operatingEndTime());
        System.out.println("breakTimes: " + request.breakTimes());
        System.out.println("imageFile: " + (imageFile != null ? imageFile.getOriginalFilename() : "null"));

        try {
            HospitalAuthResponse response = hospitalAuthService.signup(request, imageFile);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("RuntimeException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("이미지 업로드 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody HospitalLoginRequest request) {
        try {
            HospitalAuthResponse response = hospitalAuthService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        try {
            boolean isTaken = hospitalAuthService.isUsernameTaken(username);
            if (isTaken) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("이미 사용 중인 아이디입니다."));
            }
            return ResponseEntity.ok(new SuccessResponse("사용 가능한 아이디입니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/check-hospital-number")
    public ResponseEntity<?> checkHospitalNumber(@RequestParam String hospitalNumber) {
        try {
            boolean isTaken = hospitalAuthService.isHospitalNumberTaken(hospitalNumber);
            if (isTaken) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("이미 등록된 병원 번호입니다."));
            }
            return ResponseEntity.ok(new SuccessResponse("사용 가능한 병원 번호입니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/check-business-number")
    public ResponseEntity<?> checkBusinessNumber(@RequestParam String businessRegistrationNumber) {
        try {
            boolean isTaken = hospitalAuthService.isBusinessRegistrationNumberTaken(businessRegistrationNumber);
            if (isTaken) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("이미 등록된 사업자 번호입니다."));
            }
            return ResponseEntity.ok(new SuccessResponse("사용 가능한 사업자 번호입니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/update-details")
    public ResponseEntity<?> updateDetails(
            @RequestHeader("Authorization") String authorization,
            @ModelAttribute HospitalUpdateRequest request,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            String token = authorization.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);

            HospitalAuthResponse response = hospitalAuthService.updateDetailsWithImage(username, request, imageFile);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("이미지 업로드 중 오류가 발생했습니다."));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentHospital(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);

            if (username == null || !jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("유효하지 않은 토큰입니다."));
            }

            HospitalAuthResponse response = hospitalAuthService.getCurrentHospitalDetails(username);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    private record ErrorResponse(String message) {}
    private record SuccessResponse(String message) {}
}