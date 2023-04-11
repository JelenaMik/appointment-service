package com.example.appointmentservice.controllerTests;

import com.example.appointmentservice.exceptions.AppointmentHasAlreadyBooked;
import com.example.appointmentservice.exceptions.AppointmentNotFoundException;
import com.example.appointmentservice.exceptions.BookingTimeOverlapping;
import com.example.appointmentservice.repositories.AppointmentDetailRepository;
import com.example.appointmentservice.repositories.AppointmentRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@MockBeans({
        @MockBean(AppointmentRepository.class),
        @MockBean(AppointmentDetailRepository.class)
})
class AppointmentControllerPutDeleteSpringBootTest extends ObjectsForControllerTests{
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    AppointmentDetailRepository appointmentDetailRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void bookAppointment(){
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointmentEntity));
        when(appointmentRepository.existsByClientIdAndStartTime(10L, LocalDateTime.of(2023,4,20,8,0,0))).thenReturn(false);
        when(appointmentRepository.save(appointmentWithClient)).thenReturn(appointmentWithClient);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointments/book-appointment?clientId=10&appointmentId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.clientId").exists())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void bookAppointmentNotFound(){

        when(appointmentRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointments/book-appointment?clientId=10&appointmentId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof AppointmentNotFoundException))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void bookAppointmentAlreadyBooked(){
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointmentWithClient));

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointments/book-appointment?clientId=10&appointmentId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof AppointmentHasAlreadyBooked))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void bookAppointmentTimeOverlapping(){
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointmentEntity));
        when(appointmentRepository.existsByClientIdAndStartTime(10L, LocalDateTime.of(2023,4,20,8,0,0))).thenReturn(true);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointments/book-appointment?clientId=10&appointmentId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof BookingTimeOverlapping))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void changeAppointmentTypeAppNotFound(){
        when(appointmentRepository.findById(1L)).thenReturn(Optional.ofNullable(null));
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointments/change-type?type=online&appointmentId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof AppointmentNotFoundException))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void changeAppointmentTypeSuccess(){

        when(appointmentRepository.findById(1L)).thenReturn(Optional.ofNullable(appointmentEntity));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointments/change-type?type=online&appointmentId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.appointmentType").value("ONLINE"))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void cancelAppointmentSuccess(){
        when(appointmentRepository.findById(1L)).thenReturn(Optional.ofNullable(appointmentWithClient));
        when(appointmentRepository.save(appointmentEntity)).thenReturn(appointmentEntity);


        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointments/cancel?appointmentId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.clientId").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").doesNotExist())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void cancelAppointmentNotFound(){
        when(appointmentRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/appointments/cancel?appointmentId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof AppointmentNotFoundException))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void deleteAppointmentSuccess(){
        doNothing().when(appointmentRepository).deleteById(1L);
        when(appointmentDetailRepository.findByAppointmentId(1L)).thenReturn(Optional.of(appointmentDetailEntity));
        doNothing().when(appointmentDetailRepository).deleteById(10L);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/appointments/delete?appointmentId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();
    }


}
