package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class InvalidCommandException extends AbstractBaseException {
    public InvalidCommandException() {
        super("유효하지 않은 명령어입니다.", HttpStatus.BAD_REQUEST);
    }
}
