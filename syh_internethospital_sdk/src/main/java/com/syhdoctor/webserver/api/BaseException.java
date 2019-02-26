package com.syhdoctor.webserver.api;

public class BaseException extends RuntimeException {

    private Integer code;

    public BaseException(String message, Integer code) {
        super(message);
        if (code != 1) {
            code = 0;
        }
        this.code = code;

    }

    public Integer getCode() {
        return code;
    }

    public BaseException(String message) {
        super(message);
        code = 0;
    }

}
