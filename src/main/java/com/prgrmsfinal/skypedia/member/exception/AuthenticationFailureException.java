package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class AuthenticationFailureException extends AbstractBaseException {
    public AuthenticationFailureException(String message) {
      super(message, HttpStatus.UNAUTHORIZED);;
    }
}
