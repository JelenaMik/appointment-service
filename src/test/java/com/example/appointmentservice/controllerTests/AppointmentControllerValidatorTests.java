package com.example.appointmentservice.controllerTests;

import com.example.appointmentservice.dto.AppointmentRequest;
import com.example.appointmentservice.enums.AppointmentStatus;
import com.example.appointmentservice.enums.AppointmentType;
import com.example.appointmentservice.exceptions.AppointmentNotFoundException;
import com.example.appointmentservice.repositories.AppointmentDetailRepository;
import com.example.appointmentservice.repositories.AppointmentRepository;
import com.example.appointmentservice.repositories.model.AppointmentDetailEntity;
import com.example.appointmentservice.repositories.model.AppointmentEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppointmentControllerValidatorTests {

    @Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    public static String asJsonString(AppointmentRequest appointmentRequest1) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(appointmentRequest1);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    @SneakyThrows
    void createAppointmentTestNoProviderId(){
        AppointmentRequest appointmentRequest1 = AppointmentRequest.builder()
                .startDate("2023-04-20")
                .startHour("8")
                .appointmentType("office")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/appointments/create-appointment")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(appointmentRequest1)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof ConstraintViolationException))
                .andExpect((res -> assertTrue(res.getResponse().getErrorMessage().equals("[{providerId=must not be null}]") )))
                .andReturn();

    }

    @Test
    @SneakyThrows
    void createAppointmentTestWrongDate(){
        AppointmentRequest appointmentRequest1 = AppointmentRequest.builder()
                .providerId(3L)
                .startDate("2023-03-20")
                .startHour("8")
                .appointmentType("office")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/appointments/create-appointment")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(appointmentRequest1)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof ConstraintViolationException))
                .andExpect((res -> assertTrue(res.getResponse().getErrorMessage().equals("[{startTime=must be a date in the present or in the future}]") )))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void createAppointmentTestToLongDetails(){
        //finds appointment in actual database
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointments/book-appointment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("appointmentId", "40")
                                .queryParam("clientId", "10")
                                .queryParam("details", "12345678901234567890123456789012345678901234567890123456789012345678901234567890")
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof TransactionSystemException))
                .andExpect((res -> assertTrue(res.getResponse().getErrorMessage().equals("[{details=size must be between 0 and 50}]") )))
                .andReturn();
    }







}
