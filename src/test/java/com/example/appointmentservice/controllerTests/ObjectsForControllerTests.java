package com.example.appointmentservice.controllerTests;

import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.dto.AppointmentRequest;
import com.example.appointmentservice.enums.AppointmentStatus;
import com.example.appointmentservice.enums.AppointmentType;
import com.example.appointmentservice.repositories.model.AppointmentDetailEntity;
import com.example.appointmentservice.repositories.model.AppointmentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ObjectsForControllerTests {

    AppointmentEntity appointmentEntity = new AppointmentEntity();
    AppointmentEntity appointmentEntityPast = new AppointmentEntity();
    AppointmentEntity appointmentEntityWithoutId = new AppointmentEntity();
    AppointmentDto appointmentDto = new AppointmentDto();
    AppointmentDto appointmentDtoWithoutId = new AppointmentDto();
    AppointmentRequest appointmentRequest = new AppointmentRequest();
    AppointmentDetailEntity appointmentDetailEntity = new AppointmentDetailEntity();
    AppointmentDetailEntity appointmentDetailEntityFinished = new AppointmentDetailEntity();
    AppointmentDetailEntity appointmentDetailEntityWithoutId = new AppointmentDetailEntity();
    AppointmentEntity appointmentWithClient = new AppointmentEntity();
    AppointmentEntity appointment = new AppointmentEntity();


    @BeforeEach
    void setUp(){

        appointmentEntity = AppointmentEntity.builder()
                .appointmentId(1L)
                .providerId(3L)
                .startTime(LocalDateTime.of(2023,4,20,8,0,0))
                .appointmentType(AppointmentType.OFFICE)
                .details(null)
                .build();
        appointmentEntityPast = AppointmentEntity.builder()
                .appointmentId(1L)
                .providerId(3L)
                .startTime(LocalDateTime.of(2023,2,20,8,0,0))
                .appointmentType(AppointmentType.OFFICE)
                .details(null)
                .build();
        appointmentEntityWithoutId = AppointmentEntity.builder()
                .providerId(3L)
                .startTime(LocalDateTime.of(2023,4,20,8,0,0))
                .appointmentType(AppointmentType.OFFICE)
                .details(null)
                .build();

        appointmentDto = AppointmentDto.builder()
                .appointmentId(1L)
                .providerId(3L)
                .startTime(LocalDateTime.of(2023,4,20,8,0,0))
                .appointmentType(AppointmentType.OFFICE)
                .details(null)
                .build();
        appointmentDtoWithoutId = AppointmentDto.builder()
                .providerId(3L)
                .startTime(LocalDateTime.of(2023,4,20,8,0,0))
                .appointmentType(AppointmentType.OFFICE)
                .details(null)
                .build();

        appointmentRequest = AppointmentRequest.builder()
                .providerId(3L)
                .startDate("2023-04-20")
                .startHour("8")
                .appointmentType("office")
                .build();

        appointmentDetailEntity = AppointmentDetailEntity.builder()
                .appointmentDetailId(10L)
                .appointmentId(1L)
                .status(AppointmentStatus.PENDING)
                .build();
        appointmentDetailEntityFinished = AppointmentDetailEntity.builder()
                .appointmentDetailId(10L)
                .appointmentId(1L)
                .status(AppointmentStatus.FINISHED)
                .build();
        appointmentDetailEntityWithoutId = AppointmentDetailEntity.builder()
                .appointmentId(1L)
                .status(AppointmentStatus.PENDING)
                .build();
        appointmentWithClient = AppointmentEntity.builder()
                .appointmentId(1L)
                .providerId(3L)
                .clientId(10L)
                .startTime(LocalDateTime.of(2023,4,20,8,0,0))
                .appointmentType(AppointmentType.OFFICE)
                .build();

        appointment = AppointmentEntity.builder()
                .appointmentId(1L)
                .providerId(3L)
                .startTime(LocalDateTime.of(2023,4,20,8,0,0))
                .appointmentType(AppointmentType.ONLINE)
                .details(null)
                .build();
    }

}
