package com.study.tdd.api.controller.request;

import com.study.tdd.application.query.IssueShopperToken;

public record IssueShopperTokenRequest(String email, String password) {

	public IssueShopperToken toQuery() {
		return new IssueShopperToken(email, password);
	}
}
