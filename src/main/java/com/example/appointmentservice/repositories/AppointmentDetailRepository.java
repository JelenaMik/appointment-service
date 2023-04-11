package com.example.appointmentservice.repositories;

import com.example.appointmentservice.repositories.model.AppointmentDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentDetailRepository extends JpaRepository<AppointmentDetailEntity, Long> {
    Optional<AppointmentDetailEntity> findByAppointmentId(Long appointmentId);
}
