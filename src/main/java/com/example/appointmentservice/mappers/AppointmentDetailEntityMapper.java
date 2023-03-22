package com.example.appointmentservice.mappers;

import com.example.appointmentservice.dto.AppointmentDetailDto;
import com.example.appointmentservice.repositories.model.AppointmentDetailEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppointmentDetailEntityMapper {
    AppointmentDetailEntity dtoToEntity(AppointmentDetailDto appointmentDetailDto);
    AppointmentDetailDto entityToDto(AppointmentDetailEntity appointmentDetailEntity);

}
