package com.example.timetablemicroservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@Schema(description = "Запрос для записи на приём к врачу")
public class AppointmentRequest {

    @Schema(description = "Время записи на приём в формате ISO 8601", example = "2024-04-25T11:30:00", required = true)
    private LocalDateTime time;
}