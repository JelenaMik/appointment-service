package com.example.appointmentservice;

import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.dto.AppointmentRequest;
import com.example.appointmentservice.enums.AppointmentStatus;
import com.example.appointmentservice.enums.AppointmentType;
import com.example.appointmentservice.exceptions.AppointmentHasAlreadyBooked;
import com.example.appointmentservice.exceptions.AppointmentNotFoundException;
import com.example.appointmentservice.exceptions.BookingTimeOverlapping;
import com.example.appointmentservice.repositories.AppointmentDetailRepository;
import com.example.appointmentservice.repositories.AppointmentRepository;
import com.example.appointmentservice.repositories.model.AppointmentDetailEntity;
import com.example.appointmentservice.repositories.model.AppointmentEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
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
class AppointmentControllerSpringBootTest {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    AppointmentDetailRepository appointmentDetailRepository;
    @Autowired
    private MockMvc mockMvc;


    AppointmentEntity appointmentEntity = new AppointmentEntity();
    AppointmentEntity appointmentEntityWithoutId = new AppointmentEntity();
    AppointmentDto appointmentDto = new AppointmentDto();
    AppointmentDto appointmentDtoWithoutId = new AppointmentDto();
    AppointmentRequest appointmentRequest = new AppointmentRequest();
    AppointmentDetailEntity appointmentDetailEntity = new AppointmentDetailEntity();
    AppointmentDetailEntity appointmentDetailEntityWithoutId = new AppointmentDetailEntity();
    AppointmentEntity appointmentWithClient = new AppointmentEntity();


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

        appointmentDetailEntity = AppointmentDetailEntity.builder()
                .appointmentDetailId(10L)
                .appointmentId(1L)
                .status(AppointmentStatus.PENDING)
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
    }

    @Test
    @SneakyThrows
    void createAppointmentTestSuccess(){
        when(appointmentRepository.existsByProviderIdAndStartTime(3L, LocalDateTime.of(2023,4,20,8,0,0))).thenReturn(false);
        when(appointmentRepository.save(appointmentEntityWithoutId)).thenReturn(appointmentEntity);
        when(appointmentDetailRepository.save(appointmentDetailEntityWithoutId)).thenReturn(appointmentDetailEntity);

        MvcResult result = mockMvc.perform(
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
//        when(appointmentRepository.save(appointmentEntityWithoutId)).thenReturn(appointmentEntity);
//        when(appointmentDetailRepository.save(appointmentDetailEntityWithoutId)).thenReturn(appointmentDetailEntity);

        MvcResult result = mockMvc.perform(
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

        MvcResult result = mockMvc.perform(
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
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointmentEntity));
        when(appointmentRepository.existsByClientIdAndStartTime(10L, LocalDateTime.of(2023,4,20,8,0,0))).thenReturn(false);
        when(appointmentRepository.save(appointmentWithClient)).thenReturn(appointmentWithClient);

        MvcResult result = mockMvc.perform(
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

        MvcResult result = mockMvc.perform(
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

        MvcResult result = mockMvc.perform(
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

        MvcResult result = mockMvc.perform(
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
        MvcResult result = mockMvc.perform(
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
        AppointmentEntity appointment = AppointmentEntity.builder()
                .appointmentId(1L)
                .providerId(3L)
                .startTime(LocalDateTime.of(2023,4,20,8,0,0))
                .appointmentType(AppointmentType.ONLINE)
                .details(null)
                .build();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.ofNullable(appointmentEntity));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        MvcResult result = mockMvc.perform(
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


        MvcResult result = mockMvc.perform(
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

        MvcResult result = mockMvc.perform(
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
        when(appointmentDetailRepository.findByAppointmentId(1L)).thenReturn(appointmentDetailEntity);
        doNothing().when(appointmentDetailRepository).deleteById(10L);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/appointments/delete?appointmentId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();
    }


}
