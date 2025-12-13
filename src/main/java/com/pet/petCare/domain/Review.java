package com.pet.petCare.domain;

import com.pet.petCare.domain.enums.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private boolean revisitIntention;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Review(Reservation reservation, Department department, String content, boolean revisitIntention) {
        this.reservation = reservation;
        this.hospital = reservation.getHospital();
        this.user = reservation.getUser();
        this.department = department;
        this.content = content;
        this.revisitIntention = revisitIntention;
    }
}