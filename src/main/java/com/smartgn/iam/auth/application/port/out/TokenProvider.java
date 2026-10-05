package com.smartgn.iam.auth.application.port.out;

import com.smartgn.iam.auth.domain.model.Cuenta;

import java.util.Optional;

public interface TokenProvider {

    String generarToken(Cuenta cuenta);

    /** Returns the claims if the token is authentic and not expired; empty in any other case. */
    Optional<TokenClaims> validar(String token);
}
