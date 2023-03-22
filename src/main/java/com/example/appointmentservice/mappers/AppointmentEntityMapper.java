package com.example.appointmentservice.mappers;

import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.repositories.model.AppointmentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppointmentEntityMapper {
    AppointmentEntity dtoToEntity(AppointmentDto appointmentDto);
    AppointmentDto entityToDto(AppointmentEntity appointmentEntity);
}
