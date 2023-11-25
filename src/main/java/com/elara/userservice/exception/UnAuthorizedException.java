package com.elara.userservice.exception;

import com.elara.userservice.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnAuthorizedException extends RuntimeException {

    String responseCode;

    public UnAuthorizedException(String message) {
        super(message);
        this.responseCode = ResponseCode.UN_AUTHORIZED.getValue();

    }

    public UnAuthorizedException(String responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }
}
