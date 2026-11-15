package com.example.demo.exception;

public class CustomException extends RuntimeException {
    private String code;
    private String msg;

    public CustomException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    String getCode() {
        return code;
    }

    String getMsg() {
        return msg;
    }
}
