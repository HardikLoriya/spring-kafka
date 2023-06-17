package com.spring.kafka.stock.repository;

import org.springframework.data.repository.CrudRepository;

import com.spring.kafka.stock.domain.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
