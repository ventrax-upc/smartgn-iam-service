package com.smartgn.iam.auth.application.port.out;

import com.smartgn.iam.auth.domain.model.RecuperacionContrasena;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RecuperacionContrasenaRepository {

    RecuperacionContrasena save(RecuperacionContrasena recuperacion);

    /** Looks up by token hash and locks the row so it cannot be used twice in parallel. */
    Optional<RecuperacionContrasena> findByTokenHash(String tokenHash);

    /** Marks every pending request of the account as used. */
    void invalidarPendientes(UUID idCuenta, Instant ahora);
}
