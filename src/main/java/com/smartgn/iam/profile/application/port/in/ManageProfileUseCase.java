package com.smartgn.iam.profile.application.port.in;

import com.smartgn.iam.profile.domain.model.Perfil;

import java.util.UUID;

public interface ManageProfileUseCase {

    Perfil create(CreateProfileCommand command);

    Perfil getById(UUID profileId, Requester requester);

    Perfil getMine(UUID accountId);

    Perfil update(UpdateProfileCommand command);
}
