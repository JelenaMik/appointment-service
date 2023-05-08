package com.example.appointmentservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic testTopic(){
        return TopicBuilder.name("test").build();
    }

    @Bean
    public NewTopic responseTopic(){
        return TopicBuilder.name("response").build();
    }
}
