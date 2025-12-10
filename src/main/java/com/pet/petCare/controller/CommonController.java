package com.pet.petCare.controller;

import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.domain.enums.Department;
import com.pet.petCare.dto.AnimalTypeResponse;
import com.pet.petCare.dto.BreedFilterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommonController {

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
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "유효하지 않은 동물 종류입니다"));
        }
    }

    @GetMapping("/departments")
    public ResponseEntity<?> getDepartments() {
        List<Map<String, String>> departments = Arrays.stream(Department.values())
                .map(dept -> Map.of(
                        "code", dept.name(),
                        "description", dept.getDepartment()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("departments", departments));
    }
}