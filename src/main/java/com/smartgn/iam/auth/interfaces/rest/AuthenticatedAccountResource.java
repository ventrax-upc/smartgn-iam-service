package com.smartgn.iam.auth.interfaces.rest;

import com.smartgn.iam.auth.application.port.in.SignInResult;
import com.smartgn.iam.auth.domain.model.Plan;
import com.smartgn.iam.auth.domain.model.Role;

import java.util.UUID;

public record AuthenticatedAccountResource(UUID id, String email, Role role, Plan plan, String token) {

    public static AuthenticatedAccountResource from(SignInResult result) {
        return new AuthenticatedAccountResource(
                result.cuenta().getId(), result.cuenta().getCorreo(), result.cuenta().getRol(),
                result.cuenta().getPlan(), result.token());
    }
}
