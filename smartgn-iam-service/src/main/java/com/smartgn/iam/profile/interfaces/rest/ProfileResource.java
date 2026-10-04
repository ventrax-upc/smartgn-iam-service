package com.smartgn.iam.profile.interfaces.rest;

import com.smartgn.iam.profile.domain.model.Perfil;

import java.util.UUID;

public record ProfileResource(UUID id, UUID accountId, String firstName, String lastName, String phone, String address) {

    public static ProfileResource from(Perfil perfil) {
        return new ProfileResource(perfil.getId(), perfil.getIdCuenta(), perfil.getNombres(),
                perfil.getApellidos(), perfil.getTelefono(), perfil.getDireccion());
    }
}
