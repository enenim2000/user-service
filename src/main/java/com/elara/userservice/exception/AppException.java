package com.elara.userservice.exception;

import com.elara.userservice.enums.ResponseCode;
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
