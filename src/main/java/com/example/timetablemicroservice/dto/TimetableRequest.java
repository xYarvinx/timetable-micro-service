package com.example.timetablemicroservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TimetableRequest {
    private Long hospitalId;
    private Long doctorId;
    private LocalDateTime from;
    private LocalDateTime to;
    private String room;
}
