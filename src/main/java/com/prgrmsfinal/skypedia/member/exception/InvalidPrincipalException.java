package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class InvalidPrincipalException extends AbstractBaseException {
    public InvalidPrincipalException() {
        super("유효하지 않은 회원 정보입니다.", HttpStatus.FORBIDDEN);
    }
}
