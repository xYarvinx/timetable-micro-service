package com.example.timetablemicroservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long doctorId;

    private Long patientId;

    private LocalDateTime from;

    private LocalDateTime to;

    private String room;

    @PrePersist
    @PreUpdate
    public void validateAppointment() {
        validateTime(from, to);
    }

    private void validateTime(LocalDateTime from, LocalDateTime to) {
        if (from.getMinute() % 30 != 0 || to.getMinute() % 30 != 0) {
            throw new IllegalArgumentException("Minutes should be multiple of 30");
        }

        if (from.getSecond() != 0 || to.getSecond() != 0) {
            throw new IllegalArgumentException("Seconds should be 0");
        }

        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("{to} must be greater than {from}");
        }

        long differenceInHours = Duration.between(from, to).toHours();
        if (differenceInHours > 12) {
            throw new IllegalArgumentException("Difference between {from} and {to} should not exceed 12 hours");
        }
    }
}
