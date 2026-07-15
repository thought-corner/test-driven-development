package com.study.tdd.api.controller;

import com.study.tdd.api.controller.request.IssueShopperTokenRequest;
import com.study.tdd.api.controller.response.AccessTokenCarrier;
import com.study.tdd.application.ShopperIssueTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopperIssueTokenController {

	private final ShopperIssueTokenService shopperIssueTokenService;

	@Autowired
	public ShopperIssueTokenController(ShopperIssueTokenService shopperIssueTokenService) {
		this.shopperIssueTokenService = shopperIssueTokenService;
	}

	@PostMapping("/shopper/issueToken")
	ResponseEntity<?> issueToken(@RequestBody IssueShopperTokenRequest request) {
		return shopperIssueTokenService
			.issueToken(request.toQuery())
			.map(AccessTokenCarrier::new)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.badRequest().build());
	}
}
