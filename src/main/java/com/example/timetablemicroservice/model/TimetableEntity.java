package com.example.timetablemicroservice.model;

import com.example.timetablemicroservice.exception.InvalidDataException;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "timetables")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimetableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long hospitalId;

    private Long doctorId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String room;

    @PrePersist
    @PreUpdate
    public void validateTimetable() {
        validateTime(startTime, endTime);
    }

    private void validateTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.getMinute() % 30 != 0 || endTime.getMinute() % 30 != 0) {
            throw new InvalidDataException("Минуты должны быть кратны 30.");
        }

        if (startTime.getSecond() != 0 || endTime.getSecond() != 0) {
            throw new InvalidDataException("Секунд должно быть 0");
        }

        if (!endTime.isAfter(startTime)) {
            throw new InvalidDataException("{EndTime} должно быть больше, чем {startTime}");
        }

        long differenceInHours = Duration.between(startTime, endTime).toHours();
        if (differenceInHours > 12) {
            throw new InvalidDataException("Разница между {startTime} и {EndTime} не должна превышать 12 часов.");
        }
    }
}
