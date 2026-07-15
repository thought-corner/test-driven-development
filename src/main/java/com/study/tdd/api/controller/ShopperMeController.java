package com.study.tdd.api.controller;

import java.security.Principal;
import java.util.UUID;

import com.study.tdd.api.controller.response.ShopperMeView;
import com.study.tdd.application.ShopperProfileService;
import com.study.tdd.domain.Shopper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopperMeController {

	private final ShopperProfileService shopperProfileService;

	@Autowired
	public ShopperMeController(ShopperProfileService shopperProfileService) {
		this.shopperProfileService = shopperProfileService;
	}

	@GetMapping("/shopper/me")
	ShopperMeView me(Principal user) {
		UUID id = UUID.fromString(user.getName());
		Shopper shopper = shopperProfileService.findShopper(id).orElseThrow();
		return new ShopperMeView(
			id,
			shopper.getEmail(),
			shopper.getUsername()
		);
	}
}
