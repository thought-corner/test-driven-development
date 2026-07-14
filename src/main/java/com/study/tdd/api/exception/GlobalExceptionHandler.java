package com.study.tdd.api.exception;

import com.study.tdd.application.exception.BusinessException;
import com.study.tdd.application.exception.ErrorCode;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
		ErrorCode errorCode = exception.getErrorCode();
		return ResponseEntity
			.badRequest()
			.body(new ErrorResponse(errorCode.code(), errorCode.message()));
	}
}
