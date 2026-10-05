package com.smartgn.iam.profile.infrastructure.persistence;

import com.smartgn.iam.profile.domain.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PerfilJpaRepository extends JpaRepository<Perfil, UUID> {

    boolean existsByIdCuenta(UUID idCuenta);

    Optional<Perfil> findByIdCuenta(UUID idCuenta);
}
