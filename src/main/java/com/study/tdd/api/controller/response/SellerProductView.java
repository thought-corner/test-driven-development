package com.study.tdd.api.controller.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.study.tdd.domain.Product;

public record SellerProductView(
	UUID id,
	String name,
	String imageUri,
	String description,
	BigDecimal priceAmount,
	int stockQuantity,
	LocalDateTime registeredTimeUtc
) {

	public static SellerProductView from(Product product) {
		return new SellerProductView(
			product.getId(),
			product.getName(),
			product.getImageUri(),
			product.getDescription(),
			product.getPriceAmount(),
			product.getStockQuantity(),
			product.getRegisteredTimeUtc()
		);
	}
}
