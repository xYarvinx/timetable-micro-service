package com.example.timetablemicroservice.service;

import com.example.timetablemicroservice.repository.AppoitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppoitmentRepository appoitmentRepository;


}
