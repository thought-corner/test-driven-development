package com.study.tdd.api.controller.response;

import java.util.UUID;

public record ShopperMeView(
	UUID id,
	String email,
	String username
) {
}
