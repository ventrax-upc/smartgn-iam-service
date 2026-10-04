package com.smartgn.iam.profile.domain.exception;

public class PerfilNoEncontradoException extends RuntimeException {

    public PerfilNoEncontradoException() {
        super("Profile not found");
    }
}
