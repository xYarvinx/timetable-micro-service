package com.example.timetablemicroservice.repository;

import com.example.timetablemicroservice.model.TimetableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableEntity, Long> {
    boolean existsByDoctorIdAndStartTimeAndEndTime(Long doctorId, LocalDateTime from, LocalDateTime to);

    boolean existsByDoctorIdAndStartTimeAndEndTimeAndIdNot(Long doctorId, LocalDateTime from, LocalDateTime to, Long timetableId);

    void deleteByDoctorId(Long doctorId);

    void deleteByHospitalId(Long hospitalId);

    List<TimetableEntity> findAllByHospitalId(Long hospitalId);
    List<TimetableEntity> findAllByDoctorId(Long doctorId);
    List<TimetableEntity> findAllByHospitalIdAndRoom(Long hospitalId, String room);
}
