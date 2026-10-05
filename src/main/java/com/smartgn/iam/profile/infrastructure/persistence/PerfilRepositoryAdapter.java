package com.smartgn.iam.profile.infrastructure.persistence;

import com.smartgn.iam.profile.application.port.out.PerfilRepository;
import com.smartgn.iam.profile.domain.model.Perfil;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PerfilRepositoryAdapter implements PerfilRepository {

    private final PerfilJpaRepository jpa;

    public PerfilRepositoryAdapter(PerfilJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean existsByIdCuenta(UUID idCuenta) {
        return jpa.existsByIdCuenta(idCuenta);
    }

    @Override
    public Optional<Perfil> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Perfil> findByIdCuenta(UUID idCuenta) {
        return jpa.findByIdCuenta(idCuenta);
    }

    @Override
    public Perfil save(Perfil perfil) {
        return jpa.save(perfil);
    }
}
