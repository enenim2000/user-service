package com.elara.userservice.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {

    String responseCode;

    public AppException(String message) {
        super(message);

    }

    public AppException(String responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }
}
