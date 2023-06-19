package com.kafka.consumer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
public class MultipleKafkaConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultipleKafkaConsumerApplication.class, args);
	}
	
	@Bean
    public NewTopic orders() {
        return TopicBuilder.name("test")
                .partitions(3)
                .compact()
                .build();
    }

}
