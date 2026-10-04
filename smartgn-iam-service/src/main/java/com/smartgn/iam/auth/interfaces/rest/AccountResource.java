package com.smartgn.iam.auth.interfaces.rest;

import com.smartgn.iam.auth.domain.model.Cuenta;
import com.smartgn.iam.auth.domain.model.Plan;
import com.smartgn.iam.auth.domain.model.Role;

import java.time.Instant;
import java.util.UUID;

public record AccountResource(UUID id, String email, Role role, Plan plan, Instant registeredAt) {

    public static AccountResource from(Cuenta cuenta) {
        return new AccountResource(cuenta.getId(), cuenta.getCorreo(), cuenta.getRol(),
                cuenta.getPlan(), cuenta.getFechaRegistro());
    }
}
