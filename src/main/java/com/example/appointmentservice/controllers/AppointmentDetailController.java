package com.example.appointmentservice.controllers;

import com.example.appointmentservice.dto.AppointmentDetailDto;
import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.services.AppointmentDetailService;
import com.example.appointmentservice.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1/appointment-details")
@RestController
@RequiredArgsConstructor
@Log4j2
public class AppointmentDetailController {

    private final AppointmentDetailService detailService;
    private final AppointmentService appointmentService;
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDetailDto> getAppointmentDetailsById(@PathVariable Long id){
        return new ResponseEntity<>(detailService.getAppointmentDetailByAppointmentId(id), HttpStatus.OK);
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<HttpStatus> changeStatusToFinishedByAppointmentId(@PathVariable Long id){
        if(appointmentService.isBeforeNow(id)){
            detailService.changeStatusToFinished(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }
}
