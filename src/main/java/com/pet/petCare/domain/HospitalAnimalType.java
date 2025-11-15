package com.pet.petCare.domain;

import com.pet.petCare.domain.enums.AnimalType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class HospitalAnimalType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    private AnimalType animalType;

    public HospitalAnimalType(Hospital hospital, AnimalType type) {
        this.hospital = hospital;
        this.animalType = type;
    }
}