package com.prgrmsfinal.skypedia.global.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractBaseException extends RuntimeException {
	private final HttpStatus httpStatus;

	protected AbstractBaseException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
