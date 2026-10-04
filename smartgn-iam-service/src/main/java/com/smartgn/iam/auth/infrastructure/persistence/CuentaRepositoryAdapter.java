package com.smartgn.iam.auth.infrastructure.persistence;

import com.smartgn.iam.auth.application.port.out.CuentaRepository;
import com.smartgn.iam.auth.domain.model.Cuenta;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CuentaRepositoryAdapter implements CuentaRepository {

    private final CuentaJpaRepository jpa;

    public CuentaRepositoryAdapter(CuentaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return jpa.existsByCorreo(correo);
    }

    @Override
    public Optional<Cuenta> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Cuenta> findByCorreo(String correo) {
        return jpa.findByCorreo(correo);
    }

    @Override
    public Cuenta save(Cuenta cuenta) {
        return jpa.save(cuenta);
    }
}
