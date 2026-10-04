package com.smartgn.iam.subscription.domain.exception;

public class SuscripcionNoEncontradaException extends RuntimeException {

    public SuscripcionNoEncontradaException() {
        super("Subscription not found");
    }
}
