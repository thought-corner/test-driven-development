package com.study.tdd.support;

import java.math.BigDecimal;
import java.util.function.Predicate;

import com.study.tdd.api.controller.response.SellerProductView;
import com.study.tdd.application.command.RegisterProductCommand;

import org.assertj.core.api.ThrowingConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductAssertions {

	public static ThrowingConsumer<SellerProductView> isDerivedFrom(
		RegisterProductCommand command
	) {
		return product -> {
			assertThat(product.name()).isEqualTo(command.name());
			assertThat(product.imageUri()).isEqualTo(command.imageUri());
			assertThat(product.description()).isEqualTo(command.description());
			assertThat(product.priceAmount()).matches(equalTo(command.priceAmount()));
			assertThat(product.stockQuantity()).isEqualTo(command.stockQuantity());
		};
	}

	private static Predicate<? super BigDecimal> equalTo(BigDecimal expected) {
		return actual -> actual.compareTo(expected) == 0;
	}
}
