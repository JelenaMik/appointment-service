package com.example.appointmentservice.dto;

import com.example.appointmentservice.enums.AppointmentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@Builder
public class AppointmentDto {

    private Long appointmentId;
    private Long clientId;
    private Long providerId;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startTime;
    private AppointmentType appointmentType;
    private String details;
}
