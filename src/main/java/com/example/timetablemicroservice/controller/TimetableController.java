package com.example.timetablemicroservice.controller;

import com.example.timetablemicroservice.dto.TimetableRequest;
import com.example.timetablemicroservice.dto.MessageResponse;
import com.example.timetablemicroservice.exception.ControllerExceptionHandler;
import com.example.timetablemicroservice.service.TimetableService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Timetable/")
@ControllerExceptionHandler
public class TimetableController {
    private final TimetableService appointmentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createTimetable(
            @RequestBody TimetableRequest appointmentRequest,

            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ) {
        appointmentService.createTimetable(appointmentRequest, token);
        return new MessageResponse("Новая запись в рассписании успешно создана!");
    }

    @PutMapping("/{timetableId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse updateTimetable(
            @RequestBody TimetableRequest timetableRequest,

            @PathVariable Long timetableId,

            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ) {
        appointmentService.updateTimetable(timetableRequest, token, timetableId);
        return new MessageResponse("запись в рассписании обновленна");
    }



}
