package com.smartgn.iam.auth.interfaces.rest;

import com.smartgn.iam.auth.application.port.in.PasswordRecoveryUseCase;
import com.smartgn.iam.auth.application.port.in.SignInCommand;
import com.smartgn.iam.auth.application.port.in.SignInUseCase;
import com.smartgn.iam.auth.application.port.in.SignUpCommand;
import com.smartgn.iam.auth.application.port.in.SignUpUseCase;
import com.smartgn.iam.auth.domain.model.Cuenta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Sign-up, authentication and password recovery")
public class AuthController {

    private final SignUpUseCase signUpUseCase;
    private final SignInUseCase signInUseCase;
    private final PasswordRecoveryUseCase passwordRecoveryUseCase;

    public AuthController(SignUpUseCase signUpUseCase, SignInUseCase signInUseCase,
                          PasswordRecoveryUseCase passwordRecoveryUseCase) {
        this.signUpUseCase = signUpUseCase;
        this.signInUseCase = signInUseCase;
        this.passwordRecoveryUseCase = passwordRecoveryUseCase;
    }

    @PostMapping("/sign-up")
    @SecurityRequirements
    @Operation(summary = "Register an account",
            description = "Creates an account on the FREE plan. Only the public roles PROPIETARIO and ADMINISTRADOR are allowed.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "403", description = "Role not allowed in public sign-up"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<AccountResource> signUp(@Valid @RequestBody SignUpResource resource) {
        Cuenta cuenta = signUpUseCase.execute(
                new SignUpCommand(resource.email(), resource.password(), resource.role()));
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountResource.from(cuenta));
    }

    @PostMapping("/sign-in")
    @SecurityRequirements
    @Operation(summary = "Sign in",
            description = "Validates the credentials and returns a signed JWT with the claims accountId, email, role and plan.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    public ResponseEntity<AuthenticatedAccountResource> signIn(@Valid @RequestBody SignInResource resource) {
        var result = signInUseCase.execute(new SignInCommand(resource.email(), resource.password()));
        return ResponseEntity.ok(AuthenticatedAccountResource.from(result));
    }

    @PostMapping("/forgot-password")
    @SecurityRequirements
    @Operation(summary = "Request password recovery",
            description = "Requests a temporary reset link for a registered email. Delivery requires an external email adapter, which is currently pending. "
                    + "The response is the same whether or not the account exists.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Request received"),
            @ApiResponse(responseCode = "400", description = "Email with invalid format")
    })
    public ResponseEntity<MessageResource> forgotPassword(@Valid @RequestBody ForgotPasswordResource resource) {
        passwordRecoveryUseCase.solicitar(resource.email());
        return ResponseEntity.accepted().body(new MessageResource(
                "If the email is registered, you will receive instructions to reset your password."));
    }

    @PostMapping("/reset-password")
    @SecurityRequirements
    @Operation(summary = "Reset the password",
            description = "Changes the password using the token from the recovery link. The token is single-use and expires.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data, or token invalid, expired or already used")
    })
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordResource resource) {
        passwordRecoveryUseCase.restablecer(resource.token(), resource.newPassword());
        return ResponseEntity.noContent().build();
    }
}
