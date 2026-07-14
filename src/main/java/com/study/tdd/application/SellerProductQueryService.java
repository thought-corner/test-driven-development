package com.study.tdd.application;

import java.util.List;
import java.util.Optional;

import com.study.tdd.application.query.FindSellerProduct;
import com.study.tdd.application.query.GetSellerProducts;
import com.study.tdd.domain.Product;
import com.study.tdd.infrastructure.persistence.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

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

	public List<Product> getProducts(GetSellerProducts query) {
		return repository
			.findBySellerId(query.sellerId())
			.stream()
			.sorted(comparing(Product::getRegisteredTimeUtc, reverseOrder()))
			.toList();
	}
}
