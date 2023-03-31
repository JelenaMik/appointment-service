package com.example.appointmentservice.services;

import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.dto.AppointmentRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface AppointmentService {

    AppointmentDto createNewAppointment(AppointmentRequest appointmentRequest);

    AppointmentDto bookAppointment(Long clientId, Long appointmentId, String details);

    AppointmentDto changeAppointmentType(String appointmentType, Long appointmentId);

    void cancelAppointmentFromProviderSide(Long appointmentId);

    AppointmentDto cancelAppointmentFromClientSide(Long appointmentId);

    Boolean isBeforeNow(Long id);
}
