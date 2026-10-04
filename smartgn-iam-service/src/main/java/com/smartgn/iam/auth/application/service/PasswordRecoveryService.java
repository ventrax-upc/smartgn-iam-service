package com.smartgn.iam.auth.application.service;

import com.smartgn.iam.auth.application.port.in.PasswordRecoveryUseCase;
import com.smartgn.iam.auth.application.port.out.CuentaRepository;
import com.smartgn.iam.auth.application.port.out.PasswordResetNotification;
import com.smartgn.iam.auth.application.port.out.PasswordResetNotifier;
import com.smartgn.iam.auth.application.port.out.RecuperacionContrasenaRepository;
import com.smartgn.iam.auth.application.port.out.ResetTokenCodec;
import com.smartgn.iam.auth.domain.exception.TokenRecuperacionInvalidoException;
import com.smartgn.iam.auth.domain.model.Cuenta;
import com.smartgn.iam.auth.domain.model.RecuperacionContrasena;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Service
public class PasswordRecoveryService implements PasswordRecoveryUseCase {

    private static final Logger log = LoggerFactory.getLogger(PasswordRecoveryService.class);

    private final CuentaRepository cuentas;
    private final RecuperacionContrasenaRepository recuperaciones;
    private final ResetTokenCodec codec;
    private final PasswordResetNotifier notifier;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final String linkBaseUrl;
    private final Duration vigencia;

    public PasswordRecoveryService(CuentaRepository cuentas,
                                   RecuperacionContrasenaRepository recuperaciones,
                                   ResetTokenCodec codec,
                                   PasswordResetNotifier notifier,
                                   PasswordEncoder passwordEncoder,
                                   Clock clock,
                                   @Value("${smartgn.password-reset.link-base-url}") String linkBaseUrl,
                                   @Value("${smartgn.password-reset.expiration-minutes}") long expirationMinutes) {
        this.cuentas = cuentas;
        this.recuperaciones = recuperaciones;
        this.codec = codec;
        this.notifier = notifier;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
        this.linkBaseUrl = linkBaseUrl;
        this.vigencia = Duration.ofMinutes(expirationMinutes);
    }

    // Deliberately not @Transactional: the external system is notified after the token is saved
    @Override
    public void solicitar(String email) {
        String correo = email.trim().toLowerCase(Locale.ROOT);
        Optional<Cuenta> encontrada = cuentas.findByCorreo(correo);
        if (encontrada.isEmpty()) {
            return;
        }
        Cuenta cuenta = encontrada.get();

        Instant ahora = clock.instant();
        Instant expiraEn = ahora.plus(vigencia);

        recuperaciones.invalidarPendientes(cuenta.getId(), ahora);
        String token = codec.generarToken();
        recuperaciones.save(RecuperacionContrasena.crear(cuenta.getId(), codec.hashear(token), ahora, expiraEn));

        String enlace = linkBaseUrl + "?token=" + token;
        try {
            notifier.enviar(new PasswordResetNotification(correo, token, enlace, expiraEn));
        } catch (RuntimeException e) {
            // The client response must not change if the external system fails
            log.error("Could not notify the password recovery for account {}", cuenta.getId(), e);
        }
    }

    @Override
    @Transactional
    public void restablecer(String token, String nuevaPassword) {
        RecuperacionContrasena recuperacion = recuperaciones.findByTokenHash(codec.hashear(token))
                .orElseThrow(TokenRecuperacionInvalidoException::new);

        Instant ahora = clock.instant();
        if (!recuperacion.estaVigente(ahora)) {
            throw new TokenRecuperacionInvalidoException();
        }

        Cuenta cuenta = cuentas.findById(recuperacion.getIdCuenta())
                .orElseThrow(TokenRecuperacionInvalidoException::new);

        cuenta.cambiarPasswordHash(passwordEncoder.encode(nuevaPassword));
        cuentas.save(cuenta);

        recuperacion.marcarUtilizado(ahora);
        recuperaciones.save(recuperacion);
        recuperaciones.invalidarPendientes(cuenta.getId(), ahora);
    }
}
