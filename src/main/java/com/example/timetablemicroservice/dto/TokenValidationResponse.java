package com.example.timetablemicroservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ на запрос валидации токена из rabbitMQ")
public class TokenValidationResponse {
    @Schema(description = "Результат валидации токена", example = "true", required = true)
    private boolean valid;
    @Schema(description = "Идентификатор корреляции", example = "correlationId", required = true)
    private String correlationId;

}
