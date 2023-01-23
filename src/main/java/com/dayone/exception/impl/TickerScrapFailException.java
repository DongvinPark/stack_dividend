package com.dayone.exception.impl;

import com.dayone.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class TickerScrapFailException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    @Override
    public String getMessage() {
        return "주이진 Ticker로 스크랩 하는 것에 실패했습니다.";
    }
}
