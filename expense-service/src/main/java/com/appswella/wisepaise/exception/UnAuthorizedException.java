package com.appswella.wisepaise.exception;

import org.apache.http.auth.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnAuthorizedException extends AuthenticationException {
    public UnAuthorizedException(String message) {
        super(message);
    }

    public UnAuthorizedException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'",
                resourceName,
                fieldName,
                fieldValue != null ? fieldValue : "null"));
    }
}