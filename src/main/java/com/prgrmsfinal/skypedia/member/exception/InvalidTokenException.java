package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AbstractBaseException {
    public InvalidTokenException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
