package com.elara.userservice.enums;

public enum ResponseCode {
    SUCCESSFUL("00"),
    FORBIDDEN("403"),
    UN_AUTHORIZED("401"),
    INTERNAL_SERVER_ERROR("500");

    String value;

    public String getValue(){
        return value;
    }

    ResponseCode(String value) {
        this.value = value;
    }

}
