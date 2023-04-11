package com.example.appointmentservice.controllerTests;

import com.example.appointmentservice.dto.AppointmentRequest;
import com.example.appointmentservice.exceptions.AppointmentNotFoundException;
import com.example.appointmentservice.exceptions.BookingTimeOverlapping;
import com.example.appointmentservice.repositories.AppointmentDetailRepository;
import com.example.appointmentservice.repositories.AppointmentRepository;
import com.example.appointmentservice.repositories.model.AppointmentEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@MockBeans({
        @MockBean(AppointmentRepository.class),
        @MockBean(AppointmentDetailRepository.class)
})
class AppointmentControllerGetPostSpringBootTests extends ObjectsForControllerTests {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    AppointmentDetailRepository appointmentDetailRepository;
    @Autowired
    private MockMvc mockMvc;


    @Test
    @SneakyThrows
    void createAppointmentTestSuccess(){
        when(appointmentRepository.existsByProviderIdAndStartTime(3L, LocalDateTime.of(2023,4,20,8,0,0))).thenReturn(false);
        when(appointmentRepository.save(appointmentEntityWithoutId)).thenReturn(appointmentEntity);
        when(appointmentDetailRepository.save(appointmentDetailEntityWithoutId)).thenReturn(appointmentDetailEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/appointments/create-appointment")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(appointmentRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

    }

    @Test
    @SneakyThrows
    void createAppointmentTestFail(){
        when(appointmentRepository.existsByProviderIdAndStartTime(3L, LocalDateTime.of(2023,4,20,8,0,0))).thenReturn(true);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/appointments/create-appointment")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(appointmentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof BookingTimeOverlapping))
                .andReturn();

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
    void getAppointmentSuccess(){

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointmentEntity));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/appointments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.appointmentId").value(1L))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getAppointmentNotFound(){

        when(appointmentRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(
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
    void getWeekAppointmentsForClientSuccess(){
        List<AppointmentEntity> appointmentEntities = new ArrayList<>();
        appointmentEntities.add(appointmentWithClient);
        when(appointmentRepository.findAllByClientIdAndStartTimeGreaterThanEqualAndStartTimeLessThanEqualOrderByStartTimeAsc(
                10L, LocalDateTime.of(2023,4,17,0,0,0) , LocalDateTime.of(2023,4,23,23,59,59)))
                .thenReturn(appointmentEntities);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/appointments/week-appointments/16")
                                .queryParam("role", "client")
                                .queryParam("userId", "10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getWeekAppointmentsForClientEmptyList(){

        when(appointmentRepository.findAllByClientIdAndStartTimeGreaterThanEqualAndStartTimeLessThanEqualOrderByStartTimeAsc(
                10L, LocalDateTime.of(2023,4,17,0,0,0) , LocalDateTime.of(2023,4,23,23,59,59)))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/appointments/week-appointments/16")
                                .queryParam("role", "client")
                                .queryParam("userId", "10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getWeekAppointmentsForProviderSuccess(){
        List<AppointmentEntity> appointmentEntities = new ArrayList<>();
        appointmentEntities.add(appointmentWithClient);

        when(appointmentRepository.findAllByProviderIdAndStartTimeGreaterThanEqualAndStartTimeLessThanEqualOrderByStartTimeAsc(
                3L, LocalDateTime.of(2023,4,17,0,0,0) , LocalDateTime.of(2023,4,23,23,59,59)))
                .thenReturn(appointmentEntities);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/appointments/week-appointments/16")
                                .queryParam("role", "provider")
                                .queryParam("userId", "3")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getWeekAppointmentsForProviderEmptyList(){

        when(appointmentRepository.findAllByProviderIdAndStartTimeGreaterThanEqualAndStartTimeLessThanEqualOrderByStartTimeAsc(
                3L, LocalDateTime.of(2023,4,17,0,0,0) , LocalDateTime.of(2023,4,23,23,59,59)))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/appointments/week-appointments/16")
                                .queryParam("role", "provider")
                                .queryParam("userId", "3")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getWeekAppointmentsWrongRole(){

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/appointments/week-appointments/16")
                                .queryParam("role", "admin")
                                .queryParam("userId", "3")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)))
                .andReturn();
    }


}
