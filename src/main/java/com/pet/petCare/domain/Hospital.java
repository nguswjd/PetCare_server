package com.pet.petCare.domain;

import com.pet.petCare.domain.enums.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "hospitals")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String representativeName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String hospitalNumber;

    @Column(nullable = false, unique = true)
    private String businessRegistrationNumber;

    @Column(nullable = false)
    private String address;

    @Column(name = "has_parking")
    private boolean hasParking = false;

    @ElementCollection(targetClass = Department.class)
    @CollectionTable(name = "hospital_departments", joinColumns = @JoinColumn(name = "hospital_id"))
    @Column(name = "department")
    @Enumerated(EnumType.STRING)
    private List<Department> departments = new ArrayList<>();

    @ElementCollection(targetClass = AnimalType.class)
    @CollectionTable(name = "hospital_animal_types", joinColumns = @JoinColumn(name = "hospital_id"))
    @Column(name = "animal_type")
    @Enumerated(EnumType.STRING)
    private List<AnimalType> animalTypes = new ArrayList<>();

    @ElementCollection(targetClass = Breed.class)
    @CollectionTable(name = "hospital_breeds", joinColumns = @JoinColumn(name = "hospital_id"))
    @Column(name = "breed")
    @Enumerated(EnumType.STRING)
    private List<Breed> breeds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "hospital_holidays", joinColumns = @JoinColumn(name = "hospital_id"))
    @Column(name = "holiday")
    private List<LocalDate> holidays = new ArrayList<>();

    @Column(name = "operating_start_time")
    private LocalTime operatingStartTime;

    @Column(name = "operating_end_time")
    private LocalTime operatingEndTime;

    @Column(name = "is_24_hours")
    private boolean is24Hours = false;

    @ElementCollection
    @CollectionTable(name = "hospital_break_times", joinColumns = @JoinColumn(name = "hospital_id"))
    @Column(name = "break_time")
    private List<LocalTime> breakTimes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private HospitalStatus status;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 1000)
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = HospitalStatus.PENDING;
        }
    }

    public Hospital() {}

    public Hospital(String representativeName, String username, String password,
                    String name, String hospitalNumber, String businessRegistrationNumber, String address) {
        this.representativeName = representativeName;
        this.username = username;
        this.password = password;
        this.name = name;
        this.hospitalNumber = hospitalNumber;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.address = address;
    }

    @JsonProperty("operatingStatus")
    @Transient
    public HospitalOperatingStatus getOperatingStatus() {
        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

        if (holidays != null && holidays.contains(today)) {
            return HospitalOperatingStatus.CLOSED;
        }

        if (is24Hours) {
            if (breakTimes != null) {
                for (LocalTime breakStart : breakTimes) {
                    LocalTime breakEnd = breakStart.plusHours(1);
                    if (!now.isBefore(breakStart) && now.isBefore(breakEnd)) {
                        return HospitalOperatingStatus.BREAK;
                    }
                }
            }
            return HospitalOperatingStatus.OPEN_24H;
        }

        if (operatingStartTime == null || operatingEndTime == null) {
            return HospitalOperatingStatus.CLOSED;
        }

        boolean isOperatingTime = !now.isBefore(operatingStartTime) && !now.isAfter(operatingEndTime);

        if (!isOperatingTime) {
            return HospitalOperatingStatus.CLOSED;
        }

        if (breakTimes != null) {
            for (LocalTime breakStart : breakTimes) {
                LocalTime breakEnd = breakStart.plusHours(1);
                if (!now.isBefore(breakStart) && now.isBefore(breakEnd)) {
                    return HospitalOperatingStatus.BREAK;
                }
            }
        }

        return HospitalOperatingStatus.OPEN;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRepresentativeName() {
        return representativeName;
    }

    public void setRepresentativeName(String representativeName) {
        this.representativeName = representativeName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHospitalNumber() {
        return hospitalNumber;
    }

    public void setHospitalNumber(String hospitalNumber) {
        this.hospitalNumber = hospitalNumber;
    }

    public String getBusinessRegistrationNumber() {
        return businessRegistrationNumber;
    }

    public void setBusinessRegistrationNumber(String businessRegistrationNumber) {
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isHasParking() {
        return hasParking;
    }

    public void setHasParking(boolean hasParking) {
        this.hasParking = hasParking;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public List<AnimalType> getAnimalTypes() {
        return animalTypes;
    }

    public void setAnimalTypes(List<AnimalType> animalTypes) {
        this.animalTypes = animalTypes;
    }

    public List<Breed> getBreeds() {
        return breeds;
    }

    public void setBreeds(List<Breed> breeds) {
        this.breeds = breeds;
    }

    public List<LocalDate> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<LocalDate> holidays) {
        this.holidays = holidays;
    }

    public LocalTime getOperatingStartTime() {
        return operatingStartTime;
    }

    public void setOperatingStartTime(LocalTime operatingStartTime) {
        this.operatingStartTime = operatingStartTime;
    }

    public LocalTime getOperatingEndTime() {
        return operatingEndTime;
    }

    public void setOperatingEndTime(LocalTime operatingEndTime) {
        this.operatingEndTime = operatingEndTime;
    }

    public boolean isIs24Hours() {
        return is24Hours;
    }

    public void setIs24Hours(boolean is24Hours) {
        this.is24Hours = is24Hours;
    }

    public List<LocalTime> getBreakTimes() {
        return breakTimes;
    }

    public void setBreakTimes(List<LocalTime> breakTimes) {
        this.breakTimes = breakTimes;
    }

    public HospitalStatus getStatus() {
        return status;
    }

    public void setStatus(HospitalStatus status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}