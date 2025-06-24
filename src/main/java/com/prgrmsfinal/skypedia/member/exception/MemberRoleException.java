package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class MemberRoleException extends AbstractBaseException {
    public MemberRoleException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
