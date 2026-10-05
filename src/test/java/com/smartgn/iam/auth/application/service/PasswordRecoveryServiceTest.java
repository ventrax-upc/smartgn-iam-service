package com.smartgn.iam.auth.application.service;

import com.smartgn.iam.auth.application.port.out.CuentaRepository;
import com.smartgn.iam.auth.application.port.out.PasswordResetNotification;
import com.smartgn.iam.auth.application.port.out.PasswordResetNotifier;
import com.smartgn.iam.auth.application.port.out.RecuperacionContrasenaRepository;
import com.smartgn.iam.auth.application.port.out.ResetTokenCodec;
import com.smartgn.iam.auth.domain.exception.TokenRecuperacionInvalidoException;
import com.smartgn.iam.auth.domain.model.Cuenta;
import com.smartgn.iam.auth.domain.model.RecuperacionContrasena;
import com.smartgn.iam.auth.domain.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceTest {

    private static final Instant AHORA = Instant.parse("2026-10-04T10:00:00Z");

    @Mock CuentaRepository cuentas;
    @Mock RecuperacionContrasenaRepository recuperaciones;
    @Mock ResetTokenCodec codec;
    @Mock PasswordResetNotifier notifier;
    @Mock PasswordEncoder passwordEncoder;

    private PasswordRecoveryService service;
    private final UUID idCuenta = UUID.randomUUID();
    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        service = new PasswordRecoveryService(cuentas, recuperaciones, codec, notifier, passwordEncoder,
                Clock.fixed(AHORA, ZoneOffset.UTC), "http://front/reset", 30);
        cuenta = Cuenta.registrar("ana@correo.com", "OLDHASH", Role.PROPIETARIO);
        ReflectionTestUtils.setField(cuenta, "id", idCuenta);
    }

    @Test
    void solicitarGuardaSoloElHashYNotificaConElEnlace() {
        when(cuentas.findByCorreo("ana@correo.com")).thenReturn(Optional.of(cuenta));
        when(codec.generarToken()).thenReturn("TOKEN");
        when(codec.hashear("TOKEN")).thenReturn("HASH");

        service.solicitar(" Ana@Correo.com ");

        ArgumentCaptor<RecuperacionContrasena> guardada = ArgumentCaptor.forClass(RecuperacionContrasena.class);
        verify(recuperaciones).invalidarPendientes(idCuenta, AHORA);
        verify(recuperaciones).save(guardada.capture());
        assertEquals("HASH", guardada.getValue().getTokenHash());
        assertEquals(AHORA.plus(Duration.ofMinutes(30)), guardada.getValue().getExpiraEn());

        ArgumentCaptor<PasswordResetNotification> aviso = ArgumentCaptor.forClass(PasswordResetNotification.class);
        verify(notifier).enviar(aviso.capture());
        assertEquals("ana@correo.com", aviso.getValue().correo());
        assertEquals("http://front/reset?token=TOKEN", aviso.getValue().enlace());
    }

    @Test
    void solicitarConCorreoInexistenteNoHaceNadaNiFalla() {
        when(cuentas.findByCorreo("nadie@correo.com")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.solicitar("nadie@correo.com"));
        verifyNoInteractions(recuperaciones, notifier, codec);
    }

    @Test
    void solicitarNoFallaSiElSistemaExternoFalla() {
        when(cuentas.findByCorreo("ana@correo.com")).thenReturn(Optional.of(cuenta));
        when(codec.generarToken()).thenReturn("TOKEN");
        when(codec.hashear("TOKEN")).thenReturn("HASH");
        doThrow(new RuntimeException("sistema caido")).when(notifier).enviar(any());

        assertDoesNotThrow(() -> service.solicitar("ana@correo.com"));
    }

    @Test
    void restablecerCambiaLaPasswordYConsumeElToken() {
        RecuperacionContrasena pendiente =
                RecuperacionContrasena.crear(idCuenta, "HASH", AHORA.minusSeconds(60), AHORA.plusSeconds(600));
        when(codec.hashear("TOKEN")).thenReturn("HASH");
        when(recuperaciones.findByTokenHash("HASH")).thenReturn(Optional.of(pendiente));
        when(cuentas.findById(idCuenta)).thenReturn(Optional.of(cuenta));
        when(passwordEncoder.encode("NuevaClave123")).thenReturn("NEWHASH");

        service.restablecer("TOKEN", "NuevaClave123");

        assertEquals("NEWHASH", cuenta.getPasswordHash());
        assertFalse(pendiente.estaVigente(AHORA));
        verify(cuentas).save(cuenta);
        verify(recuperaciones).invalidarPendientes(idCuenta, AHORA);
    }

    @Test
    void restablecerRechazaTokenDesconocido() {
        when(codec.hashear("TOKEN")).thenReturn("HASH");
        when(recuperaciones.findByTokenHash("HASH")).thenReturn(Optional.empty());

        assertThrows(TokenRecuperacionInvalidoException.class, () -> service.restablecer("TOKEN", "NuevaClave123"));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void restablecerRechazaTokenExpirado() {
        RecuperacionContrasena expirado =
                RecuperacionContrasena.crear(idCuenta, "HASH", AHORA.minusSeconds(3600), AHORA.minusSeconds(1));
        when(codec.hashear("TOKEN")).thenReturn("HASH");
        when(recuperaciones.findByTokenHash("HASH")).thenReturn(Optional.of(expirado));

        assertThrows(TokenRecuperacionInvalidoException.class, () -> service.restablecer("TOKEN", "NuevaClave123"));
        verify(cuentas, never()).save(any());
    }

    @Test
    void restablecerRechazaTokenYaUtilizado() {
        RecuperacionContrasena usado =
                RecuperacionContrasena.crear(idCuenta, "HASH", AHORA.minusSeconds(60), AHORA.plusSeconds(600));
        usado.marcarUtilizado(AHORA.minusSeconds(10));
        when(codec.hashear("TOKEN")).thenReturn("HASH");
        when(recuperaciones.findByTokenHash("HASH")).thenReturn(Optional.of(usado));

        assertThrows(TokenRecuperacionInvalidoException.class, () -> service.restablecer("TOKEN", "NuevaClave123"));
        verify(cuentas, never()).save(any());
    }
}
