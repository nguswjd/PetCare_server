package com.pet.petCare.dto;

public record HospitalUpdateRequest(
        String representativeName,
        String name,
        String hospitalNumber,
        String address,
        Boolean hasParking,
        String departments,
        String animalTypes,
        String breeds,
        String holidays,
        String operatingStartTime,
        String operatingEndTime,
        Boolean is24Hours,
        String breakTimes
) {
    public String getRepresentativeName() {
        return representativeName;
    }

    public String getName() {
        return name;
    }

    public String getHospitalNumber() {
        return hospitalNumber;
    }

    public String getAddress() {
        return address;
    }

    public Boolean getHasParking() {
        return hasParking;
    }

    public String getAnimalTypes() {
        return animalTypes;
    }

    public String getBreeds() {
        return breeds;
    }

    public String getHolidays() {
        return holidays;
    }

    public String getOperatingStartTime() {
        return operatingStartTime;
    }

    public String getOperatingEndTime() {
        return operatingEndTime;
    }

    public Boolean getIs24Hours() {
        return is24Hours;
    }

    public String getBreakTimes() {
        return breakTimes;
    }
}