package com.example.appointmentservice.services.impl;

import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.mappers.AppointmentEntityMapper;
import com.example.appointmentservice.repositories.AppointmentRepository;
import com.example.appointmentservice.repositories.model.AppointmentEntity;
import com.example.appointmentservice.services.AppointmentViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class AppointmentViewServiceImpl implements AppointmentViewService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentEntityMapper appointmentEntityMapper;

    @Override
    public List<AppointmentDto> getAppointmentsOfCurrentWeek(Integer weekOfTheYear, String role, Long userId){
        log.info(weekOfTheYear);
        LocalDate startDate = new LocalDate().withWeekOfWeekyear(weekOfTheYear).withDayOfWeek(DateTimeConstants.MONDAY);
        LocalDateTime fromDate = LocalDateTime.of(java.time.LocalDate.parse(startDate.toString()),
                                                    LocalTime.of(0,0,0));
        log.info(fromDate);
        LocalDateTime toDate = fromDate.plusDays(7).minusSeconds(1);
        log.info(toDate);
        List<AppointmentEntity> thisWeekAppointments = new ArrayList<>();
        if(role.equals("client")){
            thisWeekAppointments = appointmentRepository.findAllByClientIdAndStartTimeGreaterThanEqualAndStartTimeLessThanEqual(userId, fromDate, toDate);
        }else if(role.equals("provider")){
            thisWeekAppointments = appointmentRepository.findAllByProviderIdAndStartTimeGreaterThanEqualAndStartTimeLessThanEqual(userId,fromDate,toDate);
        }

        return thisWeekAppointments.stream()
                .map( appointmentEntityMapper::entityToDto)
//                .map( appointment -> appointmentEntityMapper.entityToDto(appointment))
                .toList();

    }
}
