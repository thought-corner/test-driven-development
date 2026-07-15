package com.study.tdd.api.controller.response;

import java.util.UUID;

public record SellerView(UUID id, String username, String contactEmail) {
}
