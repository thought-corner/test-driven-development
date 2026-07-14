package com.study.tdd.application.query;

import java.util.UUID;

public record FindSellerProduct(UUID sellerId, UUID productId) {
}
