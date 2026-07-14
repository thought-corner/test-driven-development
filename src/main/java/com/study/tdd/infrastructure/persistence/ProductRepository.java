package com.study.tdd.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.study.tdd.domain.Product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Optional<Product> findById(UUID id);

	List<Product> findBySellerId(UUID sellerId);
}
