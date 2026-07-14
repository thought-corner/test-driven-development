package com.study.tdd.api.controller;

import java.security.Principal;
import java.util.UUID;

import com.study.tdd.api.controller.response.ArrayCarrier;
import com.study.tdd.api.controller.response.SellerProductView;
import com.study.tdd.application.SellerProductQueryService;
import com.study.tdd.application.query.GetSellerProducts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SellerProductsController {

	private final SellerProductQueryService sellerProductQueryService;

	@Autowired
	public SellerProductsController(SellerProductQueryService sellerProductQueryService) {
		this.sellerProductQueryService = sellerProductQueryService;
	}

	@GetMapping("/seller/products")
	ArrayCarrier<SellerProductView> getProducts(Principal user) {
		UUID sellerId = UUID.fromString(user.getName());
		SellerProductView[] items = sellerProductQueryService
			.getProducts(new GetSellerProducts(sellerId))
			.stream()
			.map(SellerProductView::from)
			.toArray(SellerProductView[]::new);
		return new ArrayCarrier<>(items);
	}
}
