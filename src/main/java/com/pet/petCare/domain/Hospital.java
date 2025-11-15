package com.pet.petCare.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.domain.enums.BusinessStatus;
import com.pet.petCare.domain.enums.Department;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "hospitals")
@RequiredArgsConstructor
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;

    @Enumerated(EnumType.STRING)
    private BusinessStatus status;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("hospital")
    private List<HospitalDepartment> departments = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("hospital")
    private List<HospitalAnimalType> animalTypes = new ArrayList<>();

    private LocalDate holiday;

    private boolean hasParking;

    private String imageUrl;

    private String description;

    @Builder
    public Hospital(boolean hasParking, String address) {
        this.hasParking = hasParking;
        this.address = address;
    }

    public void addDepartment(Department department) {
        HospitalDepartment hd = new HospitalDepartment(this, department);
        this.departments.add(hd);
    }

    public void addAnimalType(AnimalType type) {
        HospitalAnimalType ht = new HospitalAnimalType(this, type);
        this.animalTypes.add(ht);
    }
}