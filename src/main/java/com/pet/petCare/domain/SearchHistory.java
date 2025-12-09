package com.pet.petCare.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "search_history")
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String keyword;

    @Column(name = "searched_at", nullable = false)
    private LocalDateTime searchedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String address;

    @Column(name = "animal_type")
    private String animalType;

    private String breed;

    private String department;

    @Column(name = "has_parking")
    private Boolean hasParking;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "is_24_hours")
    private Boolean is24Hours;

    @Column(name = "result_count")
    private Integer resultCount;

    @PrePersist
    protected void onCreate() {
        this.searchedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
}