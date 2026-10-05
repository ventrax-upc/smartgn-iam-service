package com.smartgn.iam.auth.domain.exception;

public class TokenRecuperacionInvalidoException extends RuntimeException {

    public TokenRecuperacionInvalidoException() {
        super("The recovery link is invalid or has expired");
    }
}
