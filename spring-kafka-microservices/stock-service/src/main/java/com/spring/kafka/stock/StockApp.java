package com.spring.kafka.stock;

import java.util.Random;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;

import com.spring.kafka.stock.domain.Product;
import com.spring.kafka.stock.repository.ProductRepository;
import com.spring.kafka.stock.service.OrderManageService;
import com.tech.kafka.base.domain.Order;

@SpringBootApplication
@EnableKafka
public class StockApp {

	private static final Logger LOG = LoggerFactory.getLogger(StockApp.class);

	public static void main(String[] args) {
		SpringApplication.run(StockApp.class, args);
	}

	@Autowired
	OrderManageService orderManageService;

	@RetryableTopic(attempts = "4", backoff = @Backoff(delay = 1000, multiplier = 2.0), autoCreateTopics = "false")
	@KafkaListener(id = "orders", topics = "orders", groupId = "stock")
	public void onEvent(Order o) {
		LOG.info("Received: {}", o);
		if(o.getSource().equals("DLQ Kafka")) {
			 throw new RuntimeException("DLQ Kafka Error...");
		}
		if (o.getStatus().equals("NEW"))
			orderManageService.reserve(o);
		else
			orderManageService.confirm(o);

	}

	@DltHandler
	public void dlt(Order in, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		LOG.info("DLQ Error " + in + " from " + topic);
	}

	@Autowired
	private ProductRepository repository;

	@PostConstruct
	public void generateData() {
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			int count = r.nextInt(1000);
			Product p = new Product(null, "Product" + i, count, 0);
			repository.save(p);
			LOG.info("Product: {}", p);
		}
	}
}
