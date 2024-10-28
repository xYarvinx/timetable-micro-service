package com.example.timetablemicroservice.repository;

import com.example.timetablemicroservice.model.TimetableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableEntity, Long> {
    boolean existsByDoctorIdAndStartTimeAndEndTime(Long doctorId, LocalDateTime from, LocalDateTime to);

    boolean existsByDoctorIdAndStartTimeAndEndTimeAndIdNot(Long doctorId, LocalDateTime from, LocalDateTime to, Long timetableId);
}
