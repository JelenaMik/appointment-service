package com.example.appointmentservice.controllers;

import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.dto.AppointmentRequest;
import com.example.appointmentservice.services.AppointmentDetailService;
import com.example.appointmentservice.services.AppointmentService;
import com.example.appointmentservice.services.AppointmentViewService;
import jakarta.validation.executable.ValidateOnExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("api/v1/appointments")
@RestController
@RequiredArgsConstructor
@Log4j2
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final AppointmentDetailService appointmentDetailService;

    private final AppointmentViewService appointmentViewService;

    @PostMapping("/create-appointment")
    public ResponseEntity<AppointmentDto> createAppointment(@RequestBody AppointmentRequest appointmentRequest){
        AppointmentDto appointmentDto = appointmentService.createNewAppointment( appointmentRequest );
        return new ResponseEntity<>(appointmentDto, HttpStatus.CREATED);
    }

    @PutMapping("/book-appointment")
    public ResponseEntity<AppointmentDto> bookAppointment(Long clientId, Long appointmentId,  String details){
        AppointmentDto appointmentDto = appointmentService.bookAppointment(clientId, appointmentId, details);
        return new ResponseEntity<>(appointmentDto, HttpStatus.OK);
    }

    @PutMapping("/change-type")
    public ResponseEntity<AppointmentDto> changeType(String type, Long appointmentId){
        AppointmentDto appointmentDto = appointmentService.changeAppointmentType(type,appointmentId);
        return new ResponseEntity<>(appointmentDto, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> deleteAppointment(Long appointmentId){
        appointmentService.cancelAppointmentFromProviderSide(appointmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/cancel")
    public ResponseEntity<AppointmentDto> clientCancelAppointment(Long appointmentId){
        AppointmentDto appointmentDto = appointmentService.cancelAppointmentFromClientSide(appointmentId);
        return new ResponseEntity<>(appointmentDto, HttpStatus.OK);
    }

    @GetMapping("/week-appointments/{week}")
    public ResponseEntity<List<AppointmentDto>> getAppointments(@PathVariable Integer week, String role, Long userId){
        List<AppointmentDto> appointments = appointmentViewService.getAppointmentsOfCurrentWeek(week, role, userId);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

//    @GetMapping("/appointments/{week}")
//    public ResponseEntity<Map<LocalDateTime, AppointmentDto>> getAppointmentsWithNulls(@PathVariable Integer week, String role, Long userId){
//        Map<LocalDateTime, AppointmentDto> appointments = appointmentViewService.getListOfAppointmentsWithNulls(week, role, userId);
//        return new ResponseEntity<>(appointments, HttpStatus.OK);
//    }
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable Long id){
        return new ResponseEntity<>(appointmentViewService.getAppointmentById(id), HttpStatus.OK);
    }




}
