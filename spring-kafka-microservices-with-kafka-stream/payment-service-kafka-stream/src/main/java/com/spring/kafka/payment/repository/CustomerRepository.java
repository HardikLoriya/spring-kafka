package com.spring.kafka.payment.repository;

import org.springframework.data.repository.CrudRepository;

import com.spring.kafka.payment.domain.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
