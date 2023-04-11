package com.example.appointmentservice.services.impl;

import com.example.appointmentservice.dto.AppointmentDetailDto;
import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.enums.AppointmentStatus;
import com.example.appointmentservice.exceptions.AppointmentNotFoundException;
import com.example.appointmentservice.mappers.AppointmentDetailEntityMapper;
import com.example.appointmentservice.repositories.AppointmentDetailRepository;
import com.example.appointmentservice.repositories.model.AppointmentDetailEntity;
import com.example.appointmentservice.services.AppointmentDetailService;
import com.example.appointmentservice.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentDetailServiceImpl implements AppointmentDetailService {

    private final AppointmentDetailEntityMapper appointmentDetailEntityMapper;
    private final AppointmentDetailRepository appointmentDetailRepository;

    @Override
    public AppointmentDetailDto createAppointmentDetail(Long appointmentId){
        AppointmentDetailDto appointmentDetailDto = AppointmentDetailDto.builder()
                .appointmentId(appointmentId)
                .status(AppointmentStatus.PENDING)
        .build();
        log.info("appointment details was build {}", appointmentDetailDto);
        AppointmentDetailDto appointmentDetailDtoSaved = appointmentDetailEntityMapper.entityToDto(
                appointmentDetailRepository.save(
                        appointmentDetailEntityMapper.dtoToEntity(appointmentDetailDto)
                ));
        log.info("Appointment details was saved: {}", appointmentDetailDtoSaved);
        return appointmentDetailDtoSaved;
    }

    @Override
    public void deleteAppointmentDetail(Long appointmentId){
        AppointmentDetailEntity appointmentDetail = appointmentDetailRepository.findByAppointmentId(appointmentId).orElseThrow(AppointmentNotFoundException::new);
        Long id = appointmentDetail.getAppointmentDetailId();
        appointmentDetailRepository.deleteById(id);
    }

    @Override
    public AppointmentDetailDto getAppointmentDetailByAppointmentId(Long id){
        AppointmentDetailEntity appointmentDetail = appointmentDetailRepository.findByAppointmentId(id).orElseThrow(AppointmentNotFoundException::new);
        return appointmentDetailEntityMapper.entityToDto(appointmentDetail);
    }

    @Override
    public AppointmentDetailDto changeStatusToFinished(Long appointmentId){
        AppointmentDetailEntity appointmentDetail =
                appointmentDetailRepository.findByAppointmentId(appointmentId).orElseThrow(AppointmentNotFoundException::new);
                    appointmentDetail.setStatus(AppointmentStatus.FINISHED);
                    return  appointmentDetailEntityMapper.entityToDto(
                            appointmentDetailRepository.save(appointmentDetail)
                    );

    }



}
