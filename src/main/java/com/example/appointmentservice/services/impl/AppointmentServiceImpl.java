package com.example.appointmentservice.services.impl;

import com.example.appointmentservice.dto.AppointmentDetailDto;
import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.dto.AppointmentRequest;
import com.example.appointmentservice.enums.AppointmentType;
import com.example.appointmentservice.exceptions.AppointmentHasAlreadyBooked;
import com.example.appointmentservice.exceptions.AppointmentNotFoundException;
import com.example.appointmentservice.exceptions.BookingTimeOverlapping;
import com.example.appointmentservice.mappers.AppointmentEntityMapper;
import com.example.appointmentservice.repositories.AppointmentRepository;
import com.example.appointmentservice.repositories.model.AppointmentEntity;
import com.example.appointmentservice.services.AppointmentDetailService;
import com.example.appointmentservice.services.AppointmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.validation.executable.ValidateOnExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
    @Validated
    public AppointmentDto createNewAppointment(AppointmentRequest appointmentRequest) {
        int hour = Integer.parseInt( appointmentRequest.getStartHour() );

        LocalDateTime startDate = LocalDateTime.of(
                LocalDate.parse(appointmentRequest.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                LocalTime.of(hour,0,0)
        );
        log.info("start date is {}", startDate);

        if(appointmentRepository.existsByProviderIdAndStartTime(appointmentRequest.getProviderId(), startDate)) throw new BookingTimeOverlapping();

        AppointmentDto appointmentDtoSaved = appointmentMapper.entityToDto (
                appointmentRepository.save (
                        AppointmentEntity.builder()
                                .providerId(appointmentRequest.getProviderId())
                                .startTime(startDate)
                                .appointmentType(AppointmentType.valueOf(appointmentRequest.getAppointmentType().toUpperCase()))
                                .build()
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
        log.info("app was found");
        if(appointmentRepository.existsByClientIdAndStartTime(clientId, appointment.getStartTime())) throw new BookingTimeOverlapping();
        if(appointment.getClientId()!=null) throw new AppointmentHasAlreadyBooked();
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
        appointment.setClientId( null );
        appointment.setDetails( null );
        return appointmentMapper.entityToDto(
                appointmentRepository.save(appointment)
        );
    }
    @Override
    public Boolean isBeforeNow(Long id){
        AppointmentEntity appointment = appointmentRepository.findById(id).orElseThrow(AppointmentNotFoundException::new);
        LocalDateTime startDate = appointment.getStartTime();
        return startDate.isBefore(LocalDateTime.now()) || startDate.isEqual(LocalDateTime.now());
    }




}
