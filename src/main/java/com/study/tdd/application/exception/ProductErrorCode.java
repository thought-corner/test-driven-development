package com.study.tdd.application.exception;

public enum ProductErrorCode implements ErrorCode {

	INVALID_IMAGE_URI("PRODUCT_001", "상품 이미지 주소가 올바르지 않습니다.");

	private final String code;
	private final String message;

	ProductErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public String message() {
		return message;
	}
}
