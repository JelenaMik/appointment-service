package com.example.appointmentservice.repositories;

import com.example.appointmentservice.repositories.model.AppointmentDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentDetailRepository extends JpaRepository<AppointmentDetailEntity, Long> {
    AppointmentDetailEntity findByAppointmentId(Long appointmentId);
}
