package com.study.tdd.api.controller;

import java.security.Principal;
import java.util.UUID;

import com.study.tdd.api.controller.response.SellerMeView;
import com.study.tdd.domain.Seller;
import com.study.tdd.infrastructure.persistence.SellerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SellerMeController {

	private final SellerRepository repository;

	@Autowired
	public SellerMeController(SellerRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/seller/me")
	SellerMeView me(Principal user) {
		UUID id = UUID.fromString(user.getName());
		Seller seller = repository.findById(id).orElseThrow();
		return new SellerMeView(
			id,
			seller.getEmail(),
			seller.getUsername(),
			seller.getContactEmail()
		);
	}
}
