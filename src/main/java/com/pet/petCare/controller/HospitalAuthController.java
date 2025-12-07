package com.pet.petCare.controller;

import com.pet.petCare.dto.*;
import com.pet.petCare.security.JwtUtil;
import com.pet.petCare.service.HospitalAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signup(
            @RequestParam("representativeName") String representativeName,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam("hospitalNumber") String hospitalNumber,
            @RequestParam("businessRegistrationNumber") String businessRegistrationNumber,
            @RequestParam("address") String address,
            @RequestParam(value = "hasParking", required = false) Boolean hasParking,
            @RequestParam(value = "departments", required = false) String departments,
            @RequestParam(value = "animalTypes", required = false) String animalTypes,
            @RequestParam(value = "breeds", required = false) String breeds,
            @RequestParam(value = "holidays", required = false) String holidays,
            @RequestParam(value = "operatingStartTime", required = false) String operatingStartTime,
            @RequestParam(value = "operatingEndTime", required = false) String operatingEndTime,
            @RequestParam(value = "breakTimes", required = false) String breakTimes,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            HospitalSignupRequest request = new HospitalSignupRequest(
                    representativeName,
                    username,
                    password,
                    name,
                    hospitalNumber,
                    businessRegistrationNumber,
                    address,
                    hasParking,
                    departments,
                    animalTypes,
                    breeds,
                    holidays,
                    operatingStartTime,
                    operatingEndTime,
                    null,
                    breakTimes,
                    null,
                    description
            );

            HospitalAuthResponse response = hospitalAuthService.signup(request, imageFile);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IOException e) {
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

    @PutMapping(value = "/update-details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDetails(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "representativeName", required = false) String representativeName,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "hospitalNumber", required = false) String hospitalNumber,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "hasParking", required = false) Boolean hasParking,
            @RequestParam(value = "departments", required = false) String departments,
            @RequestParam(value = "animalTypes", required = false) String animalTypes,
            @RequestParam(value = "breeds", required = false) String breeds,
            @RequestParam(value = "holidays", required = false) String holidays,
            @RequestParam(value = "operatingStartTime", required = false) String operatingStartTime,
            @RequestParam(value = "operatingEndTime", required = false) String operatingEndTime,
            @RequestParam(value = "is24Hours", required = false) Boolean is24Hours,
            @RequestParam(value = "breakTimes", required = false) String breakTimes,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            String token = authorization.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);

            HospitalUpdateRequest request = new HospitalUpdateRequest(
                    representativeName,
                    name,
                    hospitalNumber,
                    address,
                    hasParking,
                    departments,
                    animalTypes,
                    breeds,
                    holidays,
                    operatingStartTime,
                    operatingEndTime,
                    is24Hours,
                    breakTimes,
                    description
            );

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

    private record ErrorResponse(String message) {}
    private record SuccessResponse(String message) {}
}