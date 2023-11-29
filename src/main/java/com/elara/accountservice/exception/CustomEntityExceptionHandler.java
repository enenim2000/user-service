package com.elara.accountservice.exception;

import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class CustomEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AppException.class)
    public final ResponseEntity<Object> handleAppException(AppException ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("responseCode", ex.getResponseCode());
        response.put("responseMessage", ex.getMessage());
        HttpStatus httpStatus = "500".equals(ex.getResponseCode()) ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.EXPECTATION_FAILED;
        return ResponseEntity.status(httpStatus).body(response);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public final ResponseEntity<Object> handleAppException(UnAuthorizedException ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("responseCode", ex.getResponseCode());
        response.put("responseMessage", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

}
