package com.example.appointmentservice.dto;

import com.example.appointmentservice.enums.AppointmentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull
    private Long providerId;
    @FutureOrPresent
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startTime;
    @Enumerated(EnumType.STRING)
    private AppointmentType appointmentType;
    @Size(max = 50)
    private String details;
}
