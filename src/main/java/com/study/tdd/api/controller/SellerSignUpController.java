package com.study.tdd.api.controller;

import com.study.tdd.api.controller.request.CreateSellerRequest;
import com.study.tdd.application.SellerSignUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record SellerSignUpController(SellerSignUpService sellerSignUpService) {

    @PostMapping("/seller/signUp")
    ResponseEntity<?> signUp(@RequestBody CreateSellerRequest request) {
        sellerSignUpService.signUp(request.toCommand());
        return ResponseEntity.noContent().build();
    }
}
