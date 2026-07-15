package com.study.tdd.api.controller;

import com.study.tdd.api.controller.response.PageCarrier;
import com.study.tdd.api.controller.response.ProductView;
import com.study.tdd.application.ProductCatalogService;
import com.study.tdd.application.query.GetProductPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopperProductsController {

	private final ProductCatalogService productCatalogService;

	@Autowired
	public ShopperProductsController(ProductCatalogService productCatalogService) {
		this.productCatalogService = productCatalogService;
	}

	@GetMapping("/shopper/products")
	PageCarrier<ProductView> getProducts(
		@RequestParam(required = false) String continuationToken
	) {
		return productCatalogService.getProductPage(new GetProductPage(continuationToken));
	}
}
