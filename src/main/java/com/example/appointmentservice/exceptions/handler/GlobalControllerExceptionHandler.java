package com.example.appointmentservice.exceptions.handler;

import com.example.appointmentservice.exceptions.AppointmentNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;

@ControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(AppointmentNotFoundException.class)
    protected ResponseEntity handleConflict(AppointmentNotFoundException e, HttpServletRequest request){
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), HttpStatus.BAD_REQUEST,
                "Bad Request", "No appointment was found", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }
}
