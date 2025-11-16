package com.pet.petCare.dto;

import com.pet.petCare.domain.enums.AnimalType;

import java.util.Arrays;
import java.util.List;

public record AnimalTypeResponse(
        List<AnimalTypeInfo> types
) {
    public static AnimalTypeResponse all() {
        List<AnimalTypeInfo> types = Arrays.stream(AnimalType.values())
                .map(type -> new AnimalTypeInfo(type.name(), type.getDescription()))
                .toList();

        return new AnimalTypeResponse(types);
    }

    public record AnimalTypeInfo(
            String code,
            String description
    ) {
    }
}