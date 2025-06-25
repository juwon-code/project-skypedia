package com.prgrmsfinal.skypedia.global.exception;

import org.springframework.http.HttpStatus;

public class SearchNotFoundException extends AbstractBaseException {
    public SearchNotFoundException() {
        super("검색 결과가 존재하지 않습니다.", HttpStatus.NOT_FOUND);;
    }
}
