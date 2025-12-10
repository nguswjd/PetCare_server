package com.pet.petCare.repository;

import com.pet.petCare.domain.Hospital;
import com.pet.petCare.domain.enums.Breed;
import com.pet.petCare.domain.enums.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    Optional<Hospital> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByHospitalNumber(String hospitalNumber);

    boolean existsByBusinessRegistrationNumber(String businessRegistrationNumber);

    @Query("SELECT DISTINCT h FROM Hospital h " +
            "LEFT JOIN h.breeds b " +
            "LEFT JOIN h.departments d " +
            "WHERE h.name LIKE %:keyword% " +
            "OR h.address LIKE %:keyword% " +
            "OR b IN :breeds " +
            "OR d IN :departments")
    List<Hospital> searchHospitals(
            @Param("keyword") String keyword,
            @Param("breeds") List<Breed> breeds,
            @Param("departments") List<Department> departments
    );
}