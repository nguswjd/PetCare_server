package com.pet.petCare.dto;

import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.domain.enums.Breed;
import lombok.Builder;

import java.util.Arrays;
import java.util.List;

@Builder
public record BreedFilterResponse(
        AnimalType animalType,
        List<BreedInfo> breeds
) {
    public static BreedFilterResponse from(AnimalType animalType) {
        List<BreedInfo> breeds = Arrays.stream(Breed.getBreedsByAnimalType(animalType))
                .map(breed -> new BreedInfo(breed.name(), breed.getDescription()))
                .toList();

        return BreedFilterResponse.builder()
                .animalType(animalType)
                .breeds(breeds)
                .build();
    }

    public record BreedInfo(
            String code,
            String description
    ) {
    }
}