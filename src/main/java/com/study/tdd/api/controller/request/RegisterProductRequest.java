package com.study.tdd.api.controller.request;

import java.math.BigDecimal;

import com.study.tdd.application.command.RegisterProductCommand;

public record RegisterProductRequest(
	String name,
	String imageUri,
	String description,
	BigDecimal priceAmount,
	int stockQuantity
) {

	public RegisterProductCommand toCommand() {
		return new RegisterProductCommand(
			name,
			imageUri,
			description,
			priceAmount,
			stockQuantity
		);
	}
}
