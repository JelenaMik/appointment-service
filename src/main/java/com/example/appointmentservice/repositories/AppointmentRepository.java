package com.example.appointmentservice.repositories;

import com.example.appointmentservice.repositories.model.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

}
