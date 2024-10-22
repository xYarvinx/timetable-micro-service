package com.example.timetablemicroservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

@Builder
@Data
@Setter
@Accessors(chain = true)
@Schema(description = "Ответ, содержащий информацию об ошибке")
public class ErrorResponse {
    @Schema(description = "Информация об ошибке", required = true, implementation = Error.class)
    private Error error;
}
