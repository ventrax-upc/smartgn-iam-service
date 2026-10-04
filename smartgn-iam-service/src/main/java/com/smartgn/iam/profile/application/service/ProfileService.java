package com.smartgn.iam.profile.application.service;

import com.smartgn.iam.profile.application.port.in.CreateProfileCommand;
import com.smartgn.iam.profile.application.port.in.ManageProfileUseCase;
import com.smartgn.iam.profile.application.port.in.Requester;
import com.smartgn.iam.profile.application.port.in.UpdateProfileCommand;
import com.smartgn.iam.profile.application.port.out.PerfilRepository;
import com.smartgn.iam.profile.domain.exception.AccesoAPerfilDenegadoException;
import com.smartgn.iam.profile.domain.exception.PerfilNoEncontradoException;
import com.smartgn.iam.profile.domain.exception.PerfilYaExisteException;
import com.smartgn.iam.profile.domain.model.Perfil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfileService implements ManageProfileUseCase {

    private final PerfilRepository perfiles;

    public ProfileService(PerfilRepository perfiles) {
        this.perfiles = perfiles;
    }

    @Override
    @Transactional
    public Perfil create(CreateProfileCommand command) {
        if (perfiles.existsByIdCuenta(command.accountId())) {
            throw new PerfilYaExisteException();
        }
        Perfil perfil = Perfil.crear(command.accountId(), command.firstName(), command.lastName(),
                command.phone(), command.address());
        return perfiles.save(perfil);
    }

    @Override
    @Transactional(readOnly = true)
    public Perfil getById(UUID profileId, Requester requester) {
        Perfil perfil = perfiles.findById(profileId).orElseThrow(PerfilNoEncontradoException::new);
        if (!perfil.perteneceA(requester.accountId()) && !requester.superAdmin()) {
            throw new AccesoAPerfilDenegadoException();
        }
        return perfil;
    }

    @Override
    @Transactional(readOnly = true)
    public Perfil getMine(UUID accountId) {
        return perfiles.findByIdCuenta(accountId).orElseThrow(PerfilNoEncontradoException::new);
    }

    @Override
    @Transactional
    public Perfil update(UpdateProfileCommand command) {
        Perfil perfil = perfiles.findById(command.profileId()).orElseThrow(PerfilNoEncontradoException::new);
        if (!perfil.perteneceA(command.requesterAccountId())) {
            throw new AccesoAPerfilDenegadoException();
        }
        perfil.actualizar(command.firstName(), command.lastName(), command.phone(), command.address());
        return perfiles.save(perfil);
    }
}
