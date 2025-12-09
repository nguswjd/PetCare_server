package com.pet.petCare.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewHistoryResponse {
    private Long id;
    private String name;
    private String address;
    private String imageUrl;
    private String operatingStatus;
    private String visitedAt;
}