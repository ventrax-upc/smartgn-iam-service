package com.smartgn.iam.auth.domain.exception;

import com.smartgn.iam.auth.domain.model.Role;

public class RolNoPermitidoException extends RuntimeException {

    public RolNoPermitidoException(Role rol) {
        super("The role " + rol + " cannot be selected in public sign-up");
    }
}
