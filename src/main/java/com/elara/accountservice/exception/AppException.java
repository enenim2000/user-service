package com.elara.accountservice.exception;

import com.elara.accountservice.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {

    String responseCode;

    public AppException(String message) {
        super(message);
        this.responseCode = ResponseCode.EXPECTATION_FAILED.getValue();
    }

    public AppException(String responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }
}
