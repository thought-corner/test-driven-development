package com.study.tdd.application;

import java.util.Optional;

import com.study.tdd.application.query.FindSellerProduct;
import com.study.tdd.domain.Product;
import com.study.tdd.infrastructure.persistence.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SellerProductQueryService {

	private final ProductRepository repository;

	@Autowired
	public SellerProductQueryService(ProductRepository repository) {
		this.repository = repository;
	}

	public Optional<Product> findProduct(FindSellerProduct query) {
		return repository
			.findById(query.productId())
			.filter(product -> product.getSellerId().equals(query.sellerId()));
	}
}
