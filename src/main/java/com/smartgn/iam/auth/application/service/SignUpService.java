package com.smartgn.iam.auth.application.service;

import com.smartgn.iam.auth.application.port.in.SignUpCommand;
import com.smartgn.iam.auth.application.port.in.SignUpUseCase;
import com.smartgn.iam.auth.application.port.out.CuentaRepository;
import com.smartgn.iam.auth.domain.exception.EmailYaRegistradoException;
import com.smartgn.iam.auth.domain.exception.RolNoPermitidoException;
import com.smartgn.iam.auth.domain.model.Cuenta;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class SignUpService implements SignUpUseCase {

    private final CuentaRepository cuentas;
    private final PasswordEncoder passwordEncoder;

    public SignUpService(CuentaRepository cuentas, PasswordEncoder passwordEncoder) {
        this.cuentas = cuentas;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Cuenta execute(SignUpCommand command) {
        if (!command.role().esPublico()) {
            throw new RolNoPermitidoException(command.role());
        }

        String correo = command.email().trim().toLowerCase(Locale.ROOT);
        if (cuentas.existsByCorreo(correo)) {
            throw new EmailYaRegistradoException(correo);
        }

        Cuenta cuenta = Cuenta.registrar(correo, passwordEncoder.encode(command.password()), command.role());
        return cuentas.save(cuenta);
    }
}
