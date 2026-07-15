package com.study.tdd.api.controller;

import com.study.tdd.api.controller.request.IssueSellerTokenRequest;
import com.study.tdd.api.controller.response.AccessTokenCarrier;
import com.study.tdd.application.SellerIssueTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SellerIssueTokenController {

	private final SellerIssueTokenService sellerIssueTokenService;

	@Autowired
	public SellerIssueTokenController(SellerIssueTokenService sellerIssueTokenService) {
		this.sellerIssueTokenService = sellerIssueTokenService;
	}

	@PostMapping("/seller/issueToken")
	ResponseEntity<?> issueToken(@RequestBody IssueSellerTokenRequest request) {
		return sellerIssueTokenService
			.issueToken(request.toQuery())
			.map(AccessTokenCarrier::new)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.badRequest().build());
	}
}
