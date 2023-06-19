package com.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
	
    @KafkaListener(topics = "test", containerFactory = "kafkaListenerContainerFactory")
    public void processMessage(@Payload String message, 
    		  @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("Message received: "+Thread.currentThread() + " : " + message);
        System.out.println("Message Partition: "+Thread.currentThread() + " : " + partition);
    }
}
