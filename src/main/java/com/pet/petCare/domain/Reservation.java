package com.pet.petCare.domain;

import com.pet.petCare.domain.enums.AnimalType;
import com.pet.petCare.domain.enums.Breed;
import com.pet.petCare.domain.enums.Department;
import com.pet.petCare.domain.enums.ReservationStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Column(nullable = false)
    private String reserverName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnimalType animalType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Breed breed;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Integer weight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private LocalTime reservationTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Reservation() {}

    public Reservation(User user, Hospital hospital, String reserverName,
                       AnimalType animalType, Breed breed, Integer age,
                       Integer weight, Department department,
                       LocalDate reservationDate, LocalTime reservationTime,
                       ReservationStatus status) {
        this.user = user;
        this.hospital = hospital;
        this.reserverName = reserverName;
        this.animalType = animalType;
        this.breed = breed;
        this.age = age;
        this.weight = weight;
        this.department = department;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.status = status != null ? status : ReservationStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public String getReserverName() {
        return reserverName;
    }

    public AnimalType getAnimalType() {
        return animalType;
    }

    public Breed getBreed() {
        return breed;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getWeight() {
        return weight;
    }

    public Department getDepartment() {
        return department;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public LocalTime getReservationTime() {
        return reservationTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public void setReserverName(String reserverName) {
        this.reserverName = reserverName;
    }

    public void setAnimalType(AnimalType animalType) {
        this.animalType = animalType;
    }

    public void setBreed(Breed breed) {
        this.breed = breed;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public void setReservationTime(LocalTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void cancel() {
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("대기 중인 예약만 취소할 수 있습니다.");
        }
        this.status = ReservationStatus.CANCELLED;
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void complete() {
        this.status = ReservationStatus.COMPLETED;
    }

    public void noShow() {
        this.status = ReservationStatus.NO_SHOW;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User user;
        private Hospital hospital;
        private String reserverName;
        private AnimalType animalType;
        private Breed breed;
        private Integer age;
        private Integer weight;
        private Department department;
        private LocalDate reservationDate;
        private LocalTime reservationTime;
        private ReservationStatus status;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder hospital(Hospital hospital) {
            this.hospital = hospital;
            return this;
        }

        public Builder reserverName(String reserverName) {
            this.reserverName = reserverName;
            return this;
        }

        public Builder animalType(AnimalType animalType) {
            this.animalType = animalType;
            return this;
        }

        public Builder breed(Breed breed) {
            this.breed = breed;
            return this;
        }

        public Builder age(Integer age) {
            this.age = age;
            return this;
        }

        public Builder weight(Integer weight) {
            this.weight = weight;
            return this;
        }

        public Builder department(Department department) {
            this.department = department;
            return this;
        }

        public Builder reservationDate(LocalDate reservationDate) {
            this.reservationDate = reservationDate;
            return this;
        }

        public Builder reservationTime(LocalTime reservationTime) {
            this.reservationTime = reservationTime;
            return this;
        }

        public Builder status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public Reservation build() {
            return new Reservation(user, hospital, reserverName, animalType,
                    breed, age, weight, department,
                    reservationDate, reservationTime, status);
        }
    }
}