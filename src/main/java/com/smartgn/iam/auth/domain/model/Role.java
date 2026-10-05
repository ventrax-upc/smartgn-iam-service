package com.smartgn.iam.auth.domain.model;

public enum Role {
    PROPIETARIO,
    ADMINISTRADOR,
    SUPERADMIN;

    /** Roles that can be chosen in public sign-up. SUPERADMIN is provisioned internally. */
    public boolean esPublico() {
        return this != SUPERADMIN;
    }
}
