package com.example.timetablemicroservice.repository;

import com.example.timetablemicroservice.model.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppoitmentRepository extends JpaRepository<AppointmentEntity, Long> {
}
