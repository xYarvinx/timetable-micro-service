package com.example.timetablemicroservice.controller;

import com.example.timetablemicroservice.dto.*;
import com.example.timetablemicroservice.exception.ControllerExceptionHandler;
import com.example.timetablemicroservice.service.TimetableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Timetable/")
@ControllerExceptionHandler
@ApiResponse(responseCode = "40*", description = "Ошибка в запросе",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)) )
@Tag(name = "Timetable Controller", description = "Контроллер для управления расписанием приёмов")
public class TimetableController {
    private final TimetableService timetableService;

    @Operation(
            summary = "Создать новую запись в расписании",
            description = "Позволяет администраторам и менеджерам создать запись для приёма врача в расписании."
    )
    @ApiResponse(responseCode = "201", description = "Новая запись в рассписании успешно создана!",
            content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createTimetable(
            @RequestBody TimetableRequest appointmentRequest,

            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ) {
        timetableService.createTimetable(appointmentRequest, token);
        return new MessageResponse("Новая запись в рассписании успешно создана!");
    }

    @Operation(
            summary = "Обновить запись в расписании",
            description = "Позволяет администраторам и менеджерам обновить существующую запись в расписании."
    )
    @ApiResponse(responseCode = "200", description = "запись в рассписании обновленна",
            content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    @PutMapping("/{timetableId}")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse updateTimetable(
            @RequestBody TimetableRequest timetableRequest,

            @PathVariable Long timetableId,

            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ) {
        timetableService.updateTimetable(timetableRequest, token, timetableId);
        return new MessageResponse("запись в рассписании обновленна");
    }

    @Operation(
            summary = "Удалить запись в расписании",
            description = "Позволяет администраторам и менеджерам удалить запись в расписании по идентификатору."
    )
    @ApiResponse(responseCode = "202", description = "запись в рассписании удалена",
            content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    @DeleteMapping("/{timetableId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MessageResponse deleteTimetable(
            @PathVariable Long timetableId,

            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ) {
        timetableService.deleteTimetable(timetableId, token);
        return new MessageResponse("запись в рассписании удалена");
    }

    @Operation(
            summary = "Удалить записи врача",
            description = "Позволяет администраторам и менеджерам удалить все записи для указанного врача."
    )
    @ApiResponse(responseCode = "202", description = "Записи успешно удалены",
            content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    @DeleteMapping("/Doctor/{doctorId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MessageResponse deleteTimetableByDoctor(
            @PathVariable Long doctorId,

            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ) {
        timetableService.deleteTimetableByDoctor(doctorId, token);
        return new MessageResponse("записи в рассписании удалены");
    }

    @Operation(
            summary = "Удалить записи больницы",
            description = "Позволяет администраторам и менеджерам удалить все записи для указанной больницы."
    )
    @ApiResponse(responseCode = "202", description = "Записи успешно удалены",
            content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    @DeleteMapping("/Hospital/{hospitalId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MessageResponse deleteTimetableByHospital(
            @PathVariable Long hospitalId,

            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ) {
        timetableService.deleteTimetableByHospital(hospitalId, token);
        return new MessageResponse("записи в рассписании удалены");
    }

    @Operation(
            summary = "Получить расписание больницы",
            description = "Позволяет авторизованным пользователям получить список записей для указанной больницы."
    )
    @ApiResponse(responseCode = "200", description = "Список записей расписания",
            content = @Content(schema = @Schema(implementation = TimetableResponse.class)))
    @GetMapping("/Hospital/{hospitalId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TimetableResponse> getTimetableByHospital(
            @PathVariable Long hospitalId
    ) {
        return timetableService.getTimetableByHospital(hospitalId);
    }

    @Operation(
            summary = "Получить расписание врача",
            description = "Позволяет авторизованным пользователям получить список записей для указанного врача."
    )
    @ApiResponse(responseCode = "200", description = "Список записей расписания",
            content = @Content(schema = @Schema(implementation = TimetableResponse.class)))
    @GetMapping("/Doctor/{doctorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TimetableResponse> getTimetableByDoctor(
            @PathVariable Long doctorId
    ) {
        return timetableService.getTimetableByDoctor(doctorId);
    }

    @Operation(
            summary = "Получить расписание кабинета",
            description = "Позволяет администраторам, менеджерам и врачам получить список записей для указанного кабинета в больнице."
    )
    @ApiResponse(responseCode = "200", description = "Список записей расписания",
            content = @Content(schema = @Schema(implementation = TimetableResponse.class)))
    @GetMapping("/Hospital/{hospitalId}/Room/{room}")
    @ResponseStatus(HttpStatus.OK)
    public List<TimetableResponse> getTimetableByHospitalAndRoom(
            @PathVariable Long hospitalId,
            @PathVariable String room,

            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ) {
        return timetableService.getTimetableByHospitalAndRoom(hospitalId, room, token);
    }

    @Operation(
            summary = "Получить свободные слоты для записи",
            description = "Позволяет авторизованным пользователям получить список доступных для записи слотов по расписанию."
    )
    @ApiResponse(responseCode = "200", description = "Список доступных слотов",
            content = @Content(schema = @Schema(implementation = LocalDateTime.class)))
    @GetMapping("/{timetableId}/Appointments")
    @ResponseStatus(HttpStatus.OK)
    public List<LocalDateTime> getTimetableByAppointment(
            @PathVariable Long timetableId
    ) {
        return timetableService.getAvailableAppointments(timetableId);
    }

    @Operation(
            summary = "Записаться на приём",
            description = "Позволяет авторизованным пользователям забронировать слот для приёма."
    )
    @ApiResponse(responseCode = "202", description = "Запись успешно создана",
            content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    @PostMapping("/{timetableId}/Appointments")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MessageResponse bookAppointment(
            @PathVariable Long timetableId,
            @RequestBody AppointmentRequest appointmentTime,

            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ) {
        timetableService.bookAppointment(timetableId, appointmentTime, token);
        return new MessageResponse("Запись на прием успешно создана!");
    }
}
