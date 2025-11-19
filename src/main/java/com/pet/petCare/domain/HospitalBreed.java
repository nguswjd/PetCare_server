package com.pet.petCare.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pet.petCare.domain.enums.Breed;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "hospital_breeds")
@NoArgsConstructor
public class HospitalBreed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    @JsonIgnoreProperties("breeds")
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    private Breed breed;

    public HospitalBreed(Hospital hospital, Breed breed) {
        this.hospital = hospital;
        this.breed = breed;
    }
}