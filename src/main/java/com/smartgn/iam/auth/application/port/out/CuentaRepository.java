package com.smartgn.iam.auth.application.port.out;

import com.smartgn.iam.auth.domain.model.Cuenta;

import java.util.Optional;
import java.util.UUID;

public interface CuentaRepository {

    boolean existsByCorreo(String correo);

    Optional<Cuenta> findById(UUID id);

    Optional<Cuenta> findByCorreo(String correo);

    Cuenta save(Cuenta cuenta);
}
