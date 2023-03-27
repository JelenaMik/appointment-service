package com.example.appointmentservice.services;

import com.example.appointmentservice.dto.AppointmentDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AppointmentViewService {
    List<AppointmentDto> getAppointmentsOfCurrentWeek(Integer weekOfTheYear, String role, Long userId);


    AppointmentDto getAppointmentById(Long appointmentId);
}
