package com.study.tdd.api.controller.response;

import java.util.UUID;

public record SellerMeView(
	UUID id,
	String email,
	String username,
	String contactEmail
) {
}
