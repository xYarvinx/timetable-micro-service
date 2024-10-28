package com.example.timetablemicroservice.controller;


import com.example.timetablemicroservice.dto.ErrorResponse;
import com.example.timetablemicroservice.dto.MessageResponse;
import com.example.timetablemicroservice.exception.ControllerExceptionHandler;
import com.example.timetablemicroservice.service.TimetableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Appointment/")
@ControllerExceptionHandler
@ApiResponse(responseCode = "40*", description = "Ошибка в запросе",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)) )
@Tag(name = "Appointment Controller", description = "Контроллер для управления записями на приём")
public class AppointmentController {
    private final TimetableService timetableService;

    @Operation(summary = "Удалить запись на приём", description = "Удаляет запись на приём по идентификатору, передаваемому в параметре `appointmentId`")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Запись успешно отменена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "У пользователя недостаточно прав для выполнения операции"),
            @ApiResponse(responseCode = "404", description = "Запись с указанным идентификатором не найдена")
    })
    @DeleteMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MessageResponse deleteAppointment(
            @PathVariable Long appointmentId,

             @Parameter(hidden = true)
             @RequestHeader("Authorization") String token
    ) {
        timetableService.deleteAppointment(appointmentId, token);
        return new MessageResponse("Запись успешно отменена");
    }
}
