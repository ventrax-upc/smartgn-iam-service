package com.smartgn.iam.auth.infrastructure.persistence;

import com.smartgn.iam.auth.domain.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CuentaJpaRepository extends JpaRepository<Cuenta, UUID> {

    boolean existsByCorreo(String correo);

    Optional<Cuenta> findByCorreo(String correo);
}
