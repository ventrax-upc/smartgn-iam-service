package com.smartgn.iam.auth.interfaces.rest;

import com.smartgn.iam.auth.domain.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpResource(
        @Schema(example = "ana@correo.com")
        @NotBlank @Email @Size(max = 254)
        String email,

        @Schema(example = "Secreta123", description = "Between 8 and 72 characters")
        @NotBlank @Size(min = 8, max = 72)
        String password,

        @Schema(description = "PROPIETARIO (owner of a home or business premises) or ADMINISTRADOR (manager of buildings or multi-site networks). SUPERADMIN is not allowed.",
                allowableValues = {"PROPIETARIO", "ADMINISTRADOR"}, example = "PROPIETARIO")
        @NotNull
        Role role) {
}
