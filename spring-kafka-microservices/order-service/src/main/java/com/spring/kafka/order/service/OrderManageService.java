package com.spring.kafka.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.spring.kafka.order.model.OrderEntity;
import com.spring.kafka.order.repository.OrderRepository;
import com.tech.kafka.base.domain.Order;

@Service
public class OrderManageService {
	
	@Autowired
	private KafkaTemplate<Long, Order> kafkaTemplate;
	
	@Autowired
	private OrderRepository orderRepository;
	
	private final Logger LOG = LoggerFactory.getLogger(OrderManageService.class);

    public Order confirm(Order orderPayment, Order orderStock) {
        Order o = new Order(orderPayment.getId(),
                orderPayment.getCustomerId(),
                orderPayment.getProductId(),
                orderPayment.getProductCount(),
                orderPayment.getPrice());
        if (orderPayment.getStatus().equals("ACCEPT") &&
                orderStock.getStatus().equals("ACCEPT")) {
            o.setStatus("CONFIRMED");
        } else if (orderPayment.getStatus().equals("REJECT") &&
                orderStock.getStatus().equals("REJECT")) {
            o.setStatus("REJECTED");
        } else if (orderPayment.getStatus().equals("REJECT") ||
                orderStock.getStatus().equals("REJECT")) {
            String source = orderPayment.getStatus().equals("REJECT")
                    ? "PAYMENT" : "STOCK";
            o.setStatus("ROLLBACK");
            o.setSource(source);
        }
        return o;
    }
    
    @KafkaListener(id = "payment-orders", topics = "payment-orders", groupId = "payment-orders")
    public void onEvent(Order o) {
    	LOG.info("Received: {}" , o);
        if (o.getStatus().equals("ACCEPT"))
        	LOG.info("Order Accepted: {}" , o);
        else
        	LOG.info("Order Rejected: {}" , o);
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(o.getId());
        orderEntity.setCustomerId(o.getCustomerId());
        orderEntity.setPrice(o.getPrice());
        orderEntity.setProductCount(o.getProductCount());
        orderEntity.setSource(o.getSource());
        orderEntity.setStatus(o.getStatus());
        orderEntity.setProductId(o.getProductId());
        orderRepository.save(orderEntity);
    }
    
    @KafkaListener(id = "stock-orders", topics = "stock-orders", groupId = "stock-orders")
    public void onOrderStockEvent(Order o) {
    	LOG.info("Received: {}" , o);
        if (o.getStatus().equals("ACCEPT")) {
        	o.setStatus("NEW");
        	kafkaTemplate.send("orders-payment", o.getId(), o);
        } else {
        	LOG.info("Order Rejected: {}" , o);
        }
    }

}
