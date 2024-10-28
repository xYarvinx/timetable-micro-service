package com.example.timetablemicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleValidationResponse {
    private boolean isValid;
    private String correlationId;
}
