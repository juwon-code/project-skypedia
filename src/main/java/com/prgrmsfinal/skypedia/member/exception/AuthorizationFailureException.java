package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class AuthorizationFailureException extends AbstractBaseException {
    public AuthorizationFailureException() {
        super("해당 리소스에 대한 접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }
}
