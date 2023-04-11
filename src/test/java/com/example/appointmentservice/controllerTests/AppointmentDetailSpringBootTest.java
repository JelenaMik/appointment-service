package com.example.appointmentservice.controllerTests;

import com.example.appointmentservice.enums.AppointmentType;
import com.example.appointmentservice.exceptions.AppointmentNotFoundException;
import com.example.appointmentservice.repositories.AppointmentDetailRepository;
import com.example.appointmentservice.repositories.AppointmentRepository;
import com.example.appointmentservice.repositories.model.AppointmentEntity;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@MockBeans({
        @MockBean(AppointmentRepository.class),
        @MockBean(AppointmentDetailRepository.class)
})
public class AppointmentDetailSpringBootTest extends ObjectsForControllerTests{
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    AppointmentDetailRepository appointmentDetailRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void getAppDetailsSuccess(){
        Mockito.when(appointmentDetailRepository.findByAppointmentId(1L)).thenReturn(Optional.of(appointmentDetailEntity));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/appointment-details/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.appointmentDetailId").exists())
                .andReturn();

    }

    @Test
    @SneakyThrows
    void getAppDetailsAppNotFound(){
        Mockito.when(appointmentDetailRepository.findByAppointmentId(1L)).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/appointment-details/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof AppointmentNotFoundException))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void changeAppStatusSuccess(){
        Mockito.when(appointmentRepository.findById(1L)).thenReturn(Optional.ofNullable(appointmentEntityPast));
        Mockito.when(appointmentDetailRepository.findByAppointmentId(1L)).thenReturn(Optional.of(appointmentDetailEntity));
        Mockito.when(appointmentDetailRepository.save(appointmentDetailEntityFinished)).thenReturn(appointmentDetailEntityFinished);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointment-details/change-status/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void changeAppStatusAfterNow(){
        Mockito.when(appointmentRepository.findById(1L)).thenReturn(Optional.ofNullable(appointmentEntity));


        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointment-details/change-status/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void changeAppStatusNoAppFound(){
        Mockito.when(appointmentRepository.findById(1L)).thenReturn(Optional.ofNullable(null));


        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointment-details/change-status/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof AppointmentNotFoundException))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void changeAppStatusNoAppDetailsFound(){
        Mockito.when(appointmentRepository.findById(1L)).thenReturn(Optional.ofNullable(appointmentEntityPast));
        Mockito.when(appointmentDetailRepository.findByAppointmentId(1L)).thenReturn(Optional.ofNullable(null));


        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointment-details/change-status/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof AppointmentNotFoundException))
                .andReturn();
    }


}
