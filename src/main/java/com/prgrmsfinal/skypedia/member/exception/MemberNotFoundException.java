package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends AbstractBaseException {
    public MemberNotFoundException() {
        super("해당 회원이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
    }
}
