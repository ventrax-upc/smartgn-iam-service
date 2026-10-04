package com.smartgn.iam.profile.domain.exception;

public class PerfilYaExisteException extends RuntimeException {

    public PerfilYaExisteException() {
        super("The account already has a registered profile");
    }
}
