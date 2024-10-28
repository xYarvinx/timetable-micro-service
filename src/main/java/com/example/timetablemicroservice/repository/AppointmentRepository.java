package com.example.timetablemicroservice.repository;

import com.example.timetablemicroservice.model.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findByTimetableId(Long timetableId);


    List<AppointmentEntity> findByTimetableIdAndPatientIdIsNull(Long timetableId);

    boolean existsByTimetableIdAndPatientIdIsNotNull(Long timetableId);

    void deleteByTimetableId(Long timetableId);
}
