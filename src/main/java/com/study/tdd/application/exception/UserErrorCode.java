package com.study.tdd.application.exception;

public enum UserErrorCode implements ErrorCode {

	INVALID_COMMAND("USER_001", "사용자 속성이 올바르지 않습니다."),
	DUPLICATE_USER_PROPERTY("USER_002", "이미 사용 중인 이메일 또는 사용자이름입니다.");

	private final String code;
	private final String message;

	UserErrorCode(String code, String message) {
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
