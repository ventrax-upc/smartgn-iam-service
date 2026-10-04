package com.smartgn.iam.profile.application.port.out;

import com.smartgn.iam.profile.domain.model.Perfil;

import java.util.Optional;
import java.util.UUID;

public interface PerfilRepository {

    boolean existsByIdCuenta(UUID idCuenta);

    Optional<Perfil> findById(UUID id);

    Optional<Perfil> findByIdCuenta(UUID idCuenta);

    Perfil save(Perfil perfil);
}
