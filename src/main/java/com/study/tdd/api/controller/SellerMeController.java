package com.study.tdd.api.controller;

import java.security.Principal;
import java.util.UUID;

import com.study.tdd.api.controller.response.SellerMeView;
import com.study.tdd.application.SellerProfileService;
import com.study.tdd.domain.Seller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SellerMeController {

	private final SellerProfileService sellerProfileService;

	@Autowired
	public SellerMeController(SellerProfileService sellerProfileService) {
		this.sellerProfileService = sellerProfileService;
	}

	@GetMapping("/seller/me")
	SellerMeView me(Principal user) {
		UUID id = UUID.fromString(user.getName());
		Seller seller = sellerProfileService.findSeller(id).orElseThrow();
		return new SellerMeView(
			id,
			seller.getEmail(),
			seller.getUsername(),
			seller.getContactEmail()
		);
	}
}
