package com.example.timetablemicroservice.controller;

import com.example.timetablemicroservice.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TimeTableController {
    private final AppointmentService appointmentService;


}
