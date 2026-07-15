package com.study.tdd.api.controller.request;

import com.study.tdd.application.command.ChangeContactEmailCommand;

public record ChangeContactEmailRequest(String contactEmail) {

	public ChangeContactEmailCommand toCommand() {
		return new ChangeContactEmailCommand(contactEmail);
	}
}
