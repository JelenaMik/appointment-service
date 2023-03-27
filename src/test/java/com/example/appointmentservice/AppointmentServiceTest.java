package com.example.appointmentservice;

import com.example.appointmentservice.dto.AppointmentDetailDto;
import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.dto.AppointmentRequest;
import com.example.appointmentservice.enums.AppointmentType;
import com.example.appointmentservice.mappers.AppointmentEntityMapper;
import com.example.appointmentservice.repositories.AppointmentRepository;
import com.example.appointmentservice.repositories.model.AppointmentEntity;
import com.example.appointmentservice.services.AppointmentDetailService;
import com.example.appointmentservice.services.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;

import java.time.LocalDateTime;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private AppointmentEntityMapper appointmentMapper;
    @Mock
    private AppointmentDetailService appointmentDetailService;
    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    AppointmentEntity appointmentEntity = new AppointmentEntity();
    AppointmentEntity appointmentEntityWithoutId = new AppointmentEntity();
    AppointmentDto appointmentDto = new AppointmentDto();
    AppointmentDto appointmentDtoWithoutId = new AppointmentDto();
    AppointmentRequest appointmentRequest = new AppointmentRequest();
    AppointmentDetailDto appointmentDetailDto = new AppointmentDetailDto();


    @BeforeEach
    void setUp(){
        appointmentEntity = AppointmentEntity.builder()
                .appointmentId(1L)
                .providerId(3L)
                .startTime(LocalDateTime.of(2023,3,20,8,0,0))
                .appointmentType(AppointmentType.OFFICE)
                .details(null)
                .build();
        appointmentEntityWithoutId = AppointmentEntity.builder()
                .providerId(3L)
                .startTime(LocalDateTime.of(2023,3,20,8,0,0))
                .appointmentType(AppointmentType.OFFICE)
                .details(null)
                .build();

        appointmentDto = AppointmentDto.builder()
                .appointmentId(1L)
                .providerId(3L)
                .startTime(LocalDateTime.of(2023,3,20,8,0,0))
                .appointmentType(AppointmentType.OFFICE)
                .details(null)
                .build();
        appointmentDtoWithoutId = AppointmentDto.builder()
                .providerId(3L)
                .startTime(LocalDateTime.of(2023,3,20,8,0,0))
                .appointmentType(AppointmentType.OFFICE)
                .details(null)
                .build();

        appointmentRequest = AppointmentRequest.builder()
                .providerId(3L)
                .startDate("20-03-2023")
                .startHour("8")
                .appointmentType("office")
                .build();

        appointmentDetailDto = AppointmentDetailDto.builder()
                .appointmentDetailId(10L)
                .appointmentId(1L)
                .status(null)
                .build();
    }

    @Test
    public void createAppointmentTest(){

        when(appointmentMapper.entityToDto(appointmentEntity)).thenReturn(appointmentDto);
        when(appointmentRepository.save(appointmentEntityWithoutId)).thenReturn(appointmentEntity);
        when(appointmentMapper.dtoToEntity(appointmentDtoWithoutId)).thenReturn(appointmentEntityWithoutId);
        when(appointmentDetailService.createAppointmentDetail(1L)).thenReturn(appointmentDetailDto);

        var expected = appointmentDto;
        var result = appointmentService.createNewAppointment(appointmentRequest);



        assertEquals(expected, result);
    }



}
