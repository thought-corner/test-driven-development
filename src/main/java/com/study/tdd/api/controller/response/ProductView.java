package com.study.tdd.api.controller.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.study.tdd.domain.Product;
import com.study.tdd.domain.Seller;

/**
 * 구매자에게 노출되는 상품 뷰. 상품 자체의 속성과 함께, 이를 판매하는 판매자 정보({@link SellerView})를 함께 싣는다.
 */
public record ProductView(
	UUID id,
	SellerView seller,
	String name,
	String imageUri,
	String description,
	BigDecimal priceAmount,
	int stockQuantity
) {

	public static ProductView from(Product product, Seller seller) {
		return new ProductView(
			product.getId(),
			new SellerView(
				seller.getId(),
				seller.getUsername(),
				seller.getContactEmail()
			),
			product.getName(),
			product.getImageUri(),
			product.getDescription(),
			product.getPriceAmount(),
			product.getStockQuantity()
		);
	}
}
