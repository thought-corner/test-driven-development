package com.study.tdd.api.controller;

import java.security.Principal;
import java.util.UUID;

import com.study.tdd.api.controller.response.SellerProductView;
import com.study.tdd.application.SellerProductQueryService;
import com.study.tdd.application.query.FindSellerProduct;
import com.study.tdd.domain.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SellerProductController {

    private final SellerProductQueryService sellerProductQueryService;

    @Autowired
    public SellerProductController(SellerProductQueryService sellerProductQueryService) {
        this.sellerProductQueryService = sellerProductQueryService;
    }

    @GetMapping("/seller/products/{id}")
    ResponseEntity<SellerProductView> findProduct(
            @PathVariable UUID id,
            Principal user
    ) {
        UUID sellerId = UUID.fromString(user.getName());
        return ResponseEntity.of(
                sellerProductQueryService
                        .findProduct(new FindSellerProduct(sellerId, id))
                        .map(SellerProductController::toView)
        );
    }

    private static SellerProductView toView(Product product) {
        return new SellerProductView(
                product.getId(),
                product.getName(),
                product.getImageUri(),
                product.getDescription(),
                product.getPriceAmount(),
                product.getStockQuantity(),
                product.getRegisteredTimeUtc()
        );
    }
}
