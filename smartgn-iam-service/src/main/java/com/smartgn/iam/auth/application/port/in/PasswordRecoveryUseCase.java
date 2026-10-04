package com.smartgn.iam.auth.application.port.in;

public interface PasswordRecoveryUseCase {

    /** Starts the recovery. Does not reveal whether the email is registered: never fails because of an unknown email. */
    void solicitar(String email);

    /** Sets the new password if the token is valid, not expired and unused. */
    void restablecer(String token, String nuevaPassword);
}
