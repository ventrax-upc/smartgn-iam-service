package com.smartgn.iam.auth.infrastructure.persistence;

import com.smartgn.iam.auth.application.port.out.RecuperacionContrasenaRepository;
import com.smartgn.iam.auth.domain.model.RecuperacionContrasena;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RecuperacionContrasenaRepositoryAdapter implements RecuperacionContrasenaRepository {

    private final RecuperacionContrasenaJpaRepository jpa;

    public RecuperacionContrasenaRepositoryAdapter(RecuperacionContrasenaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public RecuperacionContrasena save(RecuperacionContrasena recuperacion) {
        return jpa.save(recuperacion);
    }

    @Override
    public Optional<RecuperacionContrasena> findByTokenHash(String tokenHash) {
        return jpa.findByTokenHash(tokenHash);
    }

    @Override
    @Transactional
    public void invalidarPendientes(UUID idCuenta, Instant ahora) {
        jpa.invalidarPendientes(idCuenta, ahora);
    }
}
