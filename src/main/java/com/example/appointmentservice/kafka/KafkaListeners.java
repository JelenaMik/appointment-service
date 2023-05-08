package com.example.appointmentservice.kafka;

import com.example.appointmentservice.dto.AppointmentEventDto;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Map;

@Component
@Slf4j
public class KafkaListeners {
    @KafkaListener(topics="test", groupId = "groupId")
    void listener(String data){
        System.out.println("Listener received: "+ data+ " 🎉");
    }
    @KafkaListener(topics="response", groupId = "groupId")
    void responseListener(Map<String, AppointmentEventDto> map){
        log.info("in listener");
        System.out.println("Listener received: "+ map.toString() + " 🎉");
    }
}
