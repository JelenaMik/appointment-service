package com.example.appointmentservice.services.impl;

import com.example.appointmentservice.dto.AppointmentDetailDto;
import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.dto.AppointmentRequest;
import com.example.appointmentservice.enums.AppointmentType;
import com.example.appointmentservice.exceptions.AppointmentNotFoundException;
import com.example.appointmentservice.mappers.AppointmentEntityMapper;
import com.example.appointmentservice.repositories.AppointmentRepository;
import com.example.appointmentservice.repositories.model.AppointmentEntity;
import com.example.appointmentservice.services.AppointmentDetailService;
import com.example.appointmentservice.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Log4j2
@RequiredArgsConstructor
@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentEntityMapper appointmentMapper;
    private final AppointmentDetailService appointmentDetailService;
    @Override
    public AppointmentDto createNewAppointment(AppointmentRequest appointmentRequest) {
        int hour = Integer.parseInt(appointmentRequest.getStartHour());
        LocalDateTime startDate = LocalDateTime.of(
                LocalDate.parse(appointmentRequest.getStartDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                LocalTime.of(hour,0,0)
        );

        AppointmentDto appointmentDto = AppointmentDto.builder()
                .providerId(appointmentRequest.getProviderId())
                .startTime(startDate)
                .appointmentType(AppointmentType.valueOf(appointmentRequest.getAppointmentType().toUpperCase()))
                .build();
        log.info("Appointment was build {}", appointmentDto);
        AppointmentDto appointmentDtoSaved = appointmentMapper.entityToDto(
                appointmentRepository.save(
                        appointmentMapper.dtoToEntity(appointmentDto)
                ));
        log.info("Appointment was saved {}", appointmentDtoSaved);

        AppointmentDetailDto appointmentDetailDto =
                appointmentDetailService.createAppointmentDetail(appointmentDtoSaved.getAppointmentId());
        log.info("Appointment details was saved {}", appointmentDetailDto);
        return appointmentDtoSaved;
    }

    @Override
    public AppointmentDto bookAppointment(Long clientId, Long appointmentId, String details){
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElseThrow(AppointmentNotFoundException::new);
        appointment.setClientId(clientId);
        appointment.setDetails(details);
        return appointmentMapper.entityToDto(appointmentRepository.save(appointment));

    }

    @Override
    public AppointmentDto changeAppointmentType(String appointmentType, Long appointmentId){

        AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElseThrow(AppointmentNotFoundException::new);

        appointment.setAppointmentType(AppointmentType.valueOf(appointmentType.toUpperCase()));
        return appointmentMapper.entityToDto(
                appointmentRepository.save(appointment) );
    }

    @Override
    public void cancelAppointmentFromProviderSide( Long appointmentId ){
        appointmentDetailService.deleteAppointmentDetail( appointmentId );
        appointmentRepository.deleteById( appointmentId );
        log.info("appointment deleted");
    }

    @Override
    public AppointmentDto cancelAppointmentFromClientSide(Long appointmentId){
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElseThrow(AppointmentNotFoundException::new);
        appointment.setClientId(null);
        appointment.setDetails(null);
        return appointmentMapper.entityToDto(
                appointmentRepository.save(appointment)
        );
    }


}
