package com.study.tdd.api.controller;

import java.net.URI;
import java.security.Principal;
import java.util.UUID;

import com.study.tdd.api.controller.request.RegisterProductRequest;
import com.study.tdd.application.SellerProductRegistrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SellerRegisterProductController {

    private final SellerProductRegistrationService sellerProductRegistrationService;

    @Autowired
    public SellerRegisterProductController(
            SellerProductRegistrationService sellerProductRegistrationService
    ) {
        this.sellerProductRegistrationService = sellerProductRegistrationService;
    }

    @PostMapping("/seller/products")
    ResponseEntity<?> registerProduct(
            @RequestBody RegisterProductRequest request,
            Principal user
    ) {
        UUID sellerId = UUID.fromString(user.getName());
        UUID id = sellerProductRegistrationService.registerProduct(
                sellerId,
                request.toCommand()
        );
        return ResponseEntity.created(URI.create("/seller/products/" + id)).build();
    }
}
