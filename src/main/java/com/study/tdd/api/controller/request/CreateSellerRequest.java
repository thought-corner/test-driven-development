package com.study.tdd.api.controller.request;

import com.study.tdd.application.command.CreateSellerCommand;

public record CreateSellerRequest(
	String email,
	String username,
	String password,
	String contactEmail
) {

	public CreateSellerCommand toCommand() {
		return new CreateSellerCommand(email, username, password, contactEmail);
	}
}
