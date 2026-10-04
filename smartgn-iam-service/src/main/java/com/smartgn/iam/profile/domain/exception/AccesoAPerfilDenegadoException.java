package com.smartgn.iam.profile.domain.exception;

public class AccesoAPerfilDenegadoException extends RuntimeException {

    public AccesoAPerfilDenegadoException() {
        super("You do not have permission to access this profile");
    }
}
