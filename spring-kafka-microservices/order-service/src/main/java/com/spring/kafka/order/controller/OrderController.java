package com.spring.kafka.order.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.kafka.order.model.OrderEntity;
import com.spring.kafka.order.repository.OrderRepository;
import com.spring.kafka.order.service.OrderGeneratorService;
import com.tech.kafka.base.domain.Order;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);
    private AtomicLong id = new AtomicLong();
    
    @Autowired
    private KafkaTemplate<Long, Order> template;
    
    @Autowired
    OrderGeneratorService orderGeneratorService;
    
    @Autowired
    OrderRepository orderRepository;
    
    @PostMapping
    public Order create(@RequestBody Order order) {
        order.setId(id.incrementAndGet());
        template.send("orders-stock-check", order.getId(), order);
        LOG.info("Sent: {}", order);
        return order;
    }

    @PostMapping("/generate")
    public boolean create() {
        orderGeneratorService.generate();
        return true;
    }

	@GetMapping
	public List<OrderEntity> all() {
		Iterable<OrderEntity> orders = orderRepository.findAll();
		List<OrderEntity> orderList = new ArrayList<>();
		orders.forEach(o->orderList.add(o));
		return orderList;
	}
 
}
