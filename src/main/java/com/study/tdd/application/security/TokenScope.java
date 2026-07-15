package com.study.tdd.application.security;

public enum TokenScope {

	SELLER("seller"),
	SHOPPER("shopper");

	private final String value;

	TokenScope(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}
}
