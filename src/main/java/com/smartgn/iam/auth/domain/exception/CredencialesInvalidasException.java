package com.smartgn.iam.auth.domain.exception;

public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Invalid email or password");
    }
}
