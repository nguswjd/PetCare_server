package com.pet.petCare.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Breed {
    // 육지동물
    DOG_LARGE("개 (대형)", AnimalType.TERRESTRIAL),
    DOG_MEDIUM("개 (중형)", AnimalType.TERRESTRIAL),
    DOG_SMALL("개 (소형)", AnimalType.TERRESTRIAL),
    CAT("고양이", AnimalType.TERRESTRIAL),
    COW("소", AnimalType.TERRESTRIAL),
    HORSE("말", AnimalType.TERRESTRIAL),
    PIG("돼지", AnimalType.TERRESTRIAL),
    SHEEP("양", AnimalType.TERRESTRIAL),
    RABBIT("토끼", AnimalType.TERRESTRIAL),
    HEDGEHOG("고슴도치", AnimalType.TERRESTRIAL),

    // 조류
    PARROT("앵무새", AnimalType.AVIAN),
    JAVA_SPARROW("십자매", AnimalType.AVIAN),
    CHICKEN("닭", AnimalType.AVIAN),
    CHICK("병아리", AnimalType.AVIAN),
    PIGEON("비둘기", AnimalType.AVIAN),
    CANARY("카나리아", AnimalType.AVIAN),
    PARAKEET("문조", AnimalType.AVIAN),
    DUCK("오리", AnimalType.AVIAN),
    GOOSE("거위", AnimalType.AVIAN),

    // 수생생물
    FISH("물고기", AnimalType.AQUATIC),
    TURTLE("거북이", AnimalType.AQUATIC),
    CRUSTACEAN("갑각류", AnimalType.AQUATIC),

    // 기타
    OTHER("기타", AnimalType.OTHER);

    private final String description;
    private final AnimalType animalType;

    public static Breed[] getBreedsByAnimalType(AnimalType animalType) {
        return java.util.Arrays.stream(Breed.values())
                .filter(breed -> breed.getAnimalType() == animalType)
                .toArray(Breed[]::new);
    }
}