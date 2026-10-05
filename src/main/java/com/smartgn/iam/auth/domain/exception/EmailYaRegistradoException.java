package com.smartgn.iam.auth.domain.exception;

public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException(String correo) {
        super("The email is already associated with an existing account: " + correo);
    }
}
