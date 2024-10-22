package com.example.timetablemicroservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentRequest {
    private Long doctorId;
    private Long patientId;
    private LocalDateTime from;
    private LocalDateTime to;
    private String room;
}
