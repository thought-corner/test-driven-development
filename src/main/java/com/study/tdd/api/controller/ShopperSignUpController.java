package com.study.tdd.api.controller;

import com.study.tdd.api.controller.request.CreateShopperRequest;
import com.study.tdd.application.ShopperSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopperSignUpController {

    private final ShopperSignUpService shopperSignUpService;

    @Autowired
    public ShopperSignUpController(ShopperSignUpService shopperSignUpService) {
        this.shopperSignUpService = shopperSignUpService;
    }

    @PostMapping("/shopper/signUp")
    ResponseEntity<?> signUp(@RequestBody CreateShopperRequest request) {
        shopperSignUpService.signUp(request.toCommand());
        return ResponseEntity.noContent().build();
    }
}
