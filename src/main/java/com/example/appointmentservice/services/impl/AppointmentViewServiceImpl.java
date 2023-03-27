package com.example.appointmentservice.services.impl;

import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.exceptions.AppointmentNotFoundException;
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
            thisWeekAppointments = appointmentRepository.findAllByClientIdAndStartTimeGreaterThanEqualAndStartTimeLessThanEqualOrderByStartTimeAsc(userId, fromDate, toDate);
        }else if(role.equals("provider")){
            thisWeekAppointments = appointmentRepository.findAllByProviderIdAndStartTimeGreaterThanEqualAndStartTimeLessThanEqualOrderByStartTimeAsc(userId,fromDate,toDate);
        }

        return thisWeekAppointments.stream()
                .map( appointmentEntityMapper::entityToDto)
                .toList();

    }

    @Override
    public AppointmentDto getAppointmentById(Long appointmentId){
        return appointmentEntityMapper.entityToDto(
                appointmentRepository.findById(appointmentId)
                        .orElseThrow(AppointmentNotFoundException::new));
    }

//    private List<org.joda.time.LocalDateTime> listOfDates(Integer weekOfTheYear){
//        org.joda.time.LocalDateTime monday = new org.joda.time.LocalDateTime().withWeekOfWeekyear(weekOfTheYear).withDayOfWeek(DateTimeConstants.MONDAY);
//
//        List<org.joda.time.LocalDateTime> currentWeek = new ArrayList<>();
//        for(int i=0; i<7; i++){
//            currentWeek.add(monday.plusDays(i));
//        }
//
//        List<org.joda.time.LocalDateTime> listOfDates = new ArrayList<>();
//
//        for (int i=8; i<19; i++){
//            for(org.joda.time.LocalDateTime day :currentWeek){
//                listOfDates.add(day.withHourOfDay(i));
//            }
//        }
//        return listOfDates;
//
//    }
//
//    private List<LocalDateTime> getListOfJavaDates(List<org.joda.time.LocalDateTime> listOfDates){
//        List<LocalDateTime> javaDates = new ArrayList<>();
//        for (org.joda.time.LocalDateTime date : listOfDates){
//            LocalDateTime javatime = LocalDateTime.of(
//                    date.getYear(),
//                    date.getMonthOfYear(),
//                    date.getDayOfMonth(),
//                    date.getHourOfDay(),0,0);
//            javaDates.add(javatime);
//
//        }
//        return javaDates;
//    }
//    @Override
//    public Map<LocalDateTime, AppointmentDto> getListOfAppointmentsWithNulls(Integer weekOfTheYear, String role, Long userId){
//
//        List<org.joda.time.LocalDateTime> listOfDates = listOfDates(weekOfTheYear);
//        log.info("List of Dates {}", listOfDates);
//        List<LocalDateTime> javaDates= getListOfJavaDates(listOfDates);
//        log.info("List of Java dates {}", javaDates);
//        Map<LocalDateTime, AppointmentDto> listWithNulls = new TreeMap<>();
//        log.info("List with nulls {}", listWithNulls);
//        AppointmentDto appointmentDtoNull = null;
//        List<AppointmentDto> listWithoutNulls = getAppointmentsOfCurrentWeek(weekOfTheYear, role, userId);
//        for (LocalDateTime dateTime : javaDates){
//            listWithNulls.put(dateTime, appointmentDtoNull);
//            for(AppointmentDto appointmentDto : listWithoutNulls){
//                if(dateTime.equals(appointmentDto.getStartTime())) listWithNulls.put(dateTime, appointmentDto);
//            }
//        }
//        return listWithNulls;
//    }





}
