package com.example.appointmentservice;

import com.example.appointmentservice.dto.AppointmentDetailDto;
import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.dto.AppointmentRequest;
import com.example.appointmentservice.enums.AppointmentStatus;
import com.example.appointmentservice.enums.AppointmentType;
import com.example.appointmentservice.exceptions.AppointmentNotFoundException;
import com.example.appointmentservice.mappers.AppointmentEntityMapper;
import com.example.appointmentservice.repositories.AppointmentDetailRepository;
import com.example.appointmentservice.repositories.AppointmentRepository;
import com.example.appointmentservice.repositories.model.AppointmentDetailEntity;
import com.example.appointmentservice.repositories.model.AppointmentEntity;
import com.example.appointmentservice.services.AppointmentDetailService;
import com.example.appointmentservice.services.AppointmentService;
import com.example.appointmentservice.services.impl.AppointmentServiceImpl;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@MockBeans({
        @MockBean(AppointmentRepository.class),
        @MockBean(AppointmentDetailRepository.class)
})
class AppointmentControllerSpringBootTest {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    AppointmentDetailRepository appointmentDetailRepository;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MockMvc mockMvc;


    AppointmentEntity appointmentEntity = new AppointmentEntity();
    AppointmentEntity appointmentEntityWithoutId = new AppointmentEntity();
    AppointmentDto appointmentDto = new AppointmentDto();
    AppointmentDto appointmentDtoWithoutId = new AppointmentDto();
    AppointmentRequest appointmentRequest = new AppointmentRequest();
    AppointmentDetailEntity appointmentDetailEntity = new AppointmentDetailEntity();
    AppointmentDetailEntity appointmentDetailEntityWithoutId = new AppointmentDetailEntity();


    @BeforeEach
    void setUp(){

        appointmentEntity = AppointmentEntity.builder()
                .appointmentId(1L)
                .providerId(3L)
                .startTime(LocalDateTime.of(2023,4,20,8,0,0))
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

//        appointmentRequest = AppointmentRequest.builder()
//                .providerId(3L)
//                .startDate("20-03-2023")
//                .startHour("8")
//                .appointmentType("office")
//                .build();

        appointmentDetailEntity = AppointmentDetailEntity.builder()
                .appointmentDetailId(10L)
                .appointmentId(1L)
                .status(AppointmentStatus.PENDING)
                .build();
        appointmentDetailEntityWithoutId = AppointmentDetailEntity.builder()
                .appointmentId(1L)
                .status(AppointmentStatus.PENDING)
                .build();
    }

    @Test
    @SneakyThrows
    void createAppointmentTestSuccess(){

        when(appointmentRepository.save(appointmentEntityWithoutId)).thenReturn(appointmentEntity);
        when(appointmentDetailRepository.save(appointmentDetailEntityWithoutId)).thenReturn(appointmentDetailEntity);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/appointments/create-appointment")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(appointmentRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

    }

    @Test
    @SneakyThrows
    void getAppointmentSuccess(){

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointmentEntity));

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/appointments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getAppointmentNotFound(){

        when(appointmentRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/appointments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof AppointmentNotFoundException))
                .andReturn();

    }

    @Test
    @SneakyThrows
    void bookAppointment(){
        AppointmentEntity appointment = AppointmentEntity.builder()
                .appointmentId(1L)
                .providerId(3L)
                .clientId(10L)
                .startTime(LocalDateTime.of(2023,4,20,8,0,0))
                .appointmentType(AppointmentType.OFFICE)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointmentEntity));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointments/book-appointment?clientId=10&appointmentId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void bookAppointmentWrongDate(){
        AppointmentEntity appointment = AppointmentEntity.builder()
                .appointmentId(1L)
                .providerId(3L)
                .startTime(LocalDateTime.of(2023,3,20,8,0,0))
                .appointmentType(AppointmentType.OFFICE)
                .build();

        AppointmentEntity appointmentClient = AppointmentEntity.builder()
                .appointmentId(1L)
                .providerId(3L)
                .clientId(10L)
                .startTime(LocalDateTime.of(2023,3,20,8,0,0))
                .appointmentType(AppointmentType.OFFICE)
                .build();


        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointmentClient)).thenThrow(AppointmentNotFoundException::new);


        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointments/book-appointment?clientId=10&appointmentId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof ConstraintViolationException))
                .andReturn();
    }



}
