package com.study.tdd.api.controller;

import java.security.Principal;
import java.util.UUID;

import com.study.tdd.api.controller.request.ChangeContactEmailRequest;
import com.study.tdd.application.SellerContactEmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SellerChangeContactEmailController {

	private final SellerContactEmailService sellerContactEmailService;

	@Autowired
	public SellerChangeContactEmailController(
		SellerContactEmailService sellerContactEmailService
	) {
		this.sellerContactEmailService = sellerContactEmailService;
	}

	@PostMapping("/seller/changeContactEmail")
	ResponseEntity<?> changeContactEmail(
		@RequestBody ChangeContactEmailRequest request,
		Principal user
	) {
		UUID sellerId = UUID.fromString(user.getName());
		sellerContactEmailService.changeContactEmail(sellerId, request.toCommand());
		return ResponseEntity.noContent().build();
	}
}
