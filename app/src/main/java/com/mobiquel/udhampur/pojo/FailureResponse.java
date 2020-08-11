package com.mobiquel.udhampur.pojo;

/**
 * Created by Navjot Singh
 * on 2/3/19.
 */

public class FailureResponse {
    private int code;
    private String message;

    public FailureResponse() {
    }

    public FailureResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
