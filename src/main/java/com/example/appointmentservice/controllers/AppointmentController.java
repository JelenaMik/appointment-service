package com.example.appointmentservice.controllers;

import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.dto.AppointmentRequest;
import com.example.appointmentservice.services.AppointmentDetailService;
import com.example.appointmentservice.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("api/v1/appointments")
@RestController
@RequiredArgsConstructor
@Log4j2
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final AppointmentDetailService appointmentDetailService;

    @PostMapping("/create-appointment")
    public ResponseEntity createAppointment(@RequestBody AppointmentRequest appointmentRequest){
        AppointmentDto appointmentDto = appointmentService.createNewAppointment( appointmentRequest );
        return new ResponseEntity<>(appointmentDto, HttpStatus.CREATED);
    }
    @PutMapping("/book-appointment")
    public ResponseEntity bookAppointment(Long clientId, Long appointmentId, String details){
        AppointmentDto appointmentDto = appointmentService.bookAppointment(clientId, appointmentId, details);
        return new ResponseEntity<>(appointmentDto, HttpStatus.OK);
    }

    @PutMapping("/change-type")
    public ResponseEntity changeType(String type, Long appointmentId){
        AppointmentDto appointmentDto = appointmentService.changeAppointmentType(type,appointmentId);
        return new ResponseEntity<>(appointmentDto, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteAppointment(Long appointmentId){
        appointmentService.cancelAppointmentFromProviderSide(appointmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/cancel")
    public ResponseEntity clientCancelAppointment(Long appointmentId){
        AppointmentDto appointmentDto = appointmentService.cancelAppointmentFromClientSide(appointmentId);
        return new ResponseEntity<>(appointmentDto, HttpStatus.OK);
    }



}
