package com.pet.petCare.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String name;
    private String phoneNumber;
    private String species;
    private String breed;
}
