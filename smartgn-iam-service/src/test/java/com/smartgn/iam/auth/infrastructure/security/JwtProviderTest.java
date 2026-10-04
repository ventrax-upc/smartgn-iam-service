package com.smartgn.iam.auth.infrastructure.security;

import com.smartgn.iam.auth.application.port.out.TokenClaims;
import com.smartgn.iam.auth.domain.model.Cuenta;
import com.smartgn.iam.auth.domain.model.Plan;
import com.smartgn.iam.auth.domain.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtProviderTest {

    private static final String SECRET = "test-secret-test-secret-test-secret-123456";
    private static final String OTHER_SECRET = "other-secret-other-secret-other-secret-123";

    private Cuenta cuentaConId() {
        Cuenta cuenta = Cuenta.registrar("ana@correo.com", "HASH", Role.ADMINISTRADOR);
        ReflectionTestUtils.setField(cuenta, "id", UUID.randomUUID());
        return cuenta;
    }

    @Test
    void tokenGeneradoSePuedeValidarYConservaLosClaims() {
        JwtProvider provider = new JwtProvider(SECRET, 15);
        Cuenta cuenta = cuentaConId();

        Optional<TokenClaims> claims = provider.validar(provider.generarToken(cuenta));

        assertTrue(claims.isPresent());
        assertEquals(cuenta.getId(), claims.get().accountId());
        assertEquals("ana@correo.com", claims.get().email());
        assertEquals(Role.ADMINISTRADOR, claims.get().role());
        assertEquals(Plan.FREE, claims.get().plan());
    }

    @Test
    void rechazaTokenExpirado() {
        JwtProvider provider = new JwtProvider(SECRET, -1);

        assertTrue(provider.validar(provider.generarToken(cuentaConId())).isEmpty());
    }

    @Test
    void rechazaTokenFirmadoConOtraClave() {
        String token = new JwtProvider(OTHER_SECRET, 15).generarToken(cuentaConId());

        assertTrue(new JwtProvider(SECRET, 15).validar(token).isEmpty());
    }

    @Test
    void rechazaTextoQueNoEsUnToken() {
        assertTrue(new JwtProvider(SECRET, 15).validar("esto-no-es-un-jwt").isEmpty());
    }
}
