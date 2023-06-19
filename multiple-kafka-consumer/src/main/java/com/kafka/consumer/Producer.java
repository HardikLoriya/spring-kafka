package com.kafka.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class Producer {

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;
	
	@PostConstruct
	public void produceMsg() {
		for(int i=0;i<50;i++) {
			kafkaTemplate.send("test",""+i, "Kafka Msg :"+i);
		}
	}
	
}
