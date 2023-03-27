package com.example.appointmentservice.services;

import com.example.appointmentservice.dto.AppointmentDetailDto;
import com.example.appointmentservice.enums.AppointmentStatus;
import org.springframework.stereotype.Service;

@Service
public interface AppointmentDetailService {
    AppointmentDetailDto createAppointmentDetail(Long appointmentId);

    void deleteAppointmentDetail(Long appointmentId);

    AppointmentDetailDto getAppointmentDetailByAppointmentId(Long id);
}
