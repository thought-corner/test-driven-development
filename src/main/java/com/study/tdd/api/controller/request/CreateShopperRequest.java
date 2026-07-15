package com.study.tdd.api.controller.request;

import com.study.tdd.application.command.CreateShopperCommand;

public record CreateShopperRequest(
	String email,
	String username,
	String password
) {

	public CreateShopperCommand toCommand() {
		return new CreateShopperCommand(email, username, password);
	}
}
