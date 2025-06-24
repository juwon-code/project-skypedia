package com.prgrmsfinal.skypedia.member.exception;

import com.prgrmsfinal.skypedia.global.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class NicknameConflictException extends AbstractBaseException {
    public NicknameConflictException() {
        super("중복된 닉네임을 사용할 수 없습니다.", HttpStatus.CONFLICT);
    }
}
