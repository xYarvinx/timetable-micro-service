package com.example.timetablemicroservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Запрос для создания или обновления записи в расписании врача в больнице")
public class TimetableRequest {

    @Schema(description = "Идентификатор больницы, где будет проходить прием", example = "1", required = true)
    private Long hospitalId;

    @Schema(description = "Идентификатор врача, проводящего прием", example = "10", required = true)
    private Long doctorId;

    @Schema(description = "Время начала приема в формате ISO 8601", example = "2024-04-25T11:30:00", required = true)
    private LocalDateTime from;

    @Schema(description = "Время окончания приема в формате ISO 8601", example = "2024-04-25T12:00:00", required = true)
    private LocalDateTime to;

    @Schema(description = "Номер кабинета, в котором будет проходить прием", example = "101", required = true)
    private String room;
}