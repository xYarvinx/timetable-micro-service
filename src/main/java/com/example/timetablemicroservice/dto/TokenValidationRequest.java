package com.example.timetablemicroservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на валидацию токена в rabbitMQ")
public class TokenValidationRequest {
    @Schema(description = "Токен для валидации", example = "token", required = true)
    private String token;
    @Schema(description = "Идентификатор корреляции", example = "correlationId", required = true)
    private String correlationId;
}
