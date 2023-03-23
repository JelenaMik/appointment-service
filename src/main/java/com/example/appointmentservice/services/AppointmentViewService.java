package com.example.appointmentservice.services;

import com.example.appointmentservice.dto.AppointmentDto;

import java.util.List;

public interface AppointmentViewService {
    List<AppointmentDto> getAppointmentsOfCurrentWeek(Integer weekOfTheYear, String role, Long userId);
}
