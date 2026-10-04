package com.smartgn.iam.auth.application.service;

import com.smartgn.iam.auth.application.port.in.SignInCommand;
import com.smartgn.iam.auth.application.port.in.SignInResult;
import com.smartgn.iam.auth.application.port.in.SignInUseCase;
import com.smartgn.iam.auth.application.port.out.CuentaRepository;
import com.smartgn.iam.auth.application.port.out.TokenProvider;
import com.smartgn.iam.auth.domain.exception.CredencialesInvalidasException;
import com.smartgn.iam.auth.domain.model.Cuenta;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
public class SignInService implements SignInUseCase {

    private final CuentaRepository cuentas;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    // Dummy hash so the response time is similar whether or not the email exists
    private final String hashFicticio;

    public SignInService(CuentaRepository cuentas, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.cuentas = cuentas;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.hashFicticio = passwordEncoder.encode("dummy-password");
    }

    @Override
    @Transactional(readOnly = true)
    public SignInResult execute(SignInCommand command) {
        String correo = command.email().trim().toLowerCase(Locale.ROOT);
        Optional<Cuenta> encontrada = cuentas.findByCorreo(correo);

        if (encontrada.isEmpty()) {
            passwordEncoder.matches(command.password(), hashFicticio);
            throw new CredencialesInvalidasException();
        }

        Cuenta cuenta = encontrada.get();
        if (!passwordEncoder.matches(command.password(), cuenta.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        return new SignInResult(cuenta, tokenProvider.generarToken(cuenta));
    }
}
