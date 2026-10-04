package com.smartgn.iam.auth.interfaces.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInResource(
        @Schema(example = "ana@correo.com")
        @NotBlank @Size(max = 254)
        String email,

        @Schema(example = "Secreta123")
        @NotBlank @Size(max = 72)
        String password) {
}
