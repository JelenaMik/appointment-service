package com.example.appointmentservice.exceptions.handler;

import com.example.appointmentservice.exceptions.AppointmentNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(AppointmentNotFoundException.class)
    protected ResponseEntity handleConflict(AppointmentNotFoundException e, HttpServletRequest request){
        ErrorModel errorModel = new ErrorModel(LocalDate.now(), HttpStatus.BAD_REQUEST,
                "Bad Request", "No appointment was found", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity handleBindErrors(ConstraintViolationException exception, HttpServletRequest request){

        List errorList = exception.getConstraintViolations().stream()
                .map(fieldError -> {
                    Map<String, String > errorMap = new HashMap<>();
                    errorMap.put(fieldError.getPropertyPath().toString(), fieldError.getMessage());
                    return errorMap;
                }).toList();

        ErrorModel errorModel = ErrorModel.builder()
                .timestamp(LocalDate.now())
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage("Bad request")
                .message(errorList.toString())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

}
