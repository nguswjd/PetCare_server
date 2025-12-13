package com.pet.petCare.dto;

public record ReviewRequest(
        Long reservationId,
        String department,
        String content,
        String revisitIntention
) {}