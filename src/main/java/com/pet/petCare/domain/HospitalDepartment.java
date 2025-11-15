package com.pet.petCare.domain;

import com.pet.petCare.domain.enums.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class HospitalDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    private Department department;

    public HospitalDepartment(Hospital hospital, Department department) {
        this.hospital = hospital;
        this.department = department;
    }
}