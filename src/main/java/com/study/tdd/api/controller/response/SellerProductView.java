package com.study.tdd.api.controller.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SellerProductView(
	UUID id,
	String name,
	String imageUri,
	String description,
	BigDecimal priceAmount,
	int stockQuantity,
	LocalDateTime registeredTimeUtc
) {
}
