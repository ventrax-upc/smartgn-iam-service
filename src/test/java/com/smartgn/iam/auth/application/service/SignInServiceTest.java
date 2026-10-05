package com.smartgn.iam.auth.application.service;

import com.smartgn.iam.auth.application.port.in.SignInCommand;
import com.smartgn.iam.auth.application.port.in.SignInResult;
import com.smartgn.iam.auth.application.port.out.CuentaRepository;
import com.smartgn.iam.auth.application.port.out.TokenProvider;
import com.smartgn.iam.auth.domain.exception.CredencialesInvalidasException;
import com.smartgn.iam.auth.domain.model.Cuenta;
import com.smartgn.iam.auth.domain.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignInServiceTest {

    @Mock CuentaRepository cuentas;
    @Mock PasswordEncoder passwordEncoder;
    @Mock TokenProvider tokenProvider;
    @InjectMocks SignInService service;

    private final Cuenta cuenta = Cuenta.registrar("ana@correo.com", "HASH", Role.PROPIETARIO);

    @Test
    void devuelveTokenConCredencialesValidas() {
        when(cuentas.findByCorreo("ana@correo.com")).thenReturn(Optional.of(cuenta));
        when(passwordEncoder.matches("Secreta123", "HASH")).thenReturn(true);
        when(tokenProvider.generarToken(cuenta)).thenReturn("JWT");

        SignInResult result = service.execute(new SignInCommand(" Ana@Correo.com ", "Secreta123"));

        assertEquals("JWT", result.token());
        assertEquals("ana@correo.com", result.cuenta().getCorreo());
    }

    @Test
    void rechazaPasswordIncorrecta() {
        when(cuentas.findByCorreo("ana@correo.com")).thenReturn(Optional.of(cuenta));
        when(passwordEncoder.matches("mala", "HASH")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class,
                () -> service.execute(new SignInCommand("ana@correo.com", "mala")));
        verifyNoInteractions(tokenProvider);
    }

    @Test
    void rechazaCorreoInexistente() {
        when(cuentas.findByCorreo("nadie@correo.com")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class,
                () -> service.execute(new SignInCommand("nadie@correo.com", "Secreta123")));
        verifyNoInteractions(tokenProvider);
    }
}
