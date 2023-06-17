package com.spring.kafka.order.repository;

import org.springframework.data.repository.CrudRepository;

import com.spring.kafka.order.model.OrderEntity;


public interface OrderRepository extends CrudRepository<OrderEntity, Long> {
}
