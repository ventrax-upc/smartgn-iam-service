package com.smartgn.iam.auth.application.service;

import com.smartgn.iam.auth.application.port.in.SignUpCommand;
import com.smartgn.iam.auth.application.port.out.CuentaRepository;
import com.smartgn.iam.auth.domain.exception.EmailYaRegistradoException;
import com.smartgn.iam.auth.domain.exception.RolNoPermitidoException;
import com.smartgn.iam.auth.domain.model.Cuenta;
import com.smartgn.iam.auth.domain.model.Plan;
import com.smartgn.iam.auth.domain.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {

    @Mock CuentaRepository cuentas;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks SignUpService service;

    @Test
    void registraCuentaConPlanFreeYPasswordHasheada() {
        when(cuentas.existsByCorreo("ana@correo.com")).thenReturn(false);
        when(passwordEncoder.encode("Secreta123")).thenReturn("HASH");
        when(cuentas.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

        Cuenta cuenta = service.execute(new SignUpCommand("  Ana@Correo.com ", "Secreta123", Role.PROPIETARIO));

        assertEquals("ana@correo.com", cuenta.getCorreo());
        assertEquals("HASH", cuenta.getPasswordHash());
        assertEquals(Role.PROPIETARIO, cuenta.getRol());
        assertEquals(Plan.FREE, cuenta.getPlan());
    }

    @Test
    void rechazaSuperAdminEnRegistroPublico() {
        assertThrows(RolNoPermitidoException.class,
                () -> service.execute(new SignUpCommand("a@b.com", "Secreta123", Role.SUPERADMIN)));
        verifyNoInteractions(cuentas);
    }

    @Test
    void rechazaCorreoDuplicado() {
        when(cuentas.existsByCorreo("ana@correo.com")).thenReturn(true);

        assertThrows(EmailYaRegistradoException.class,
                () -> service.execute(new SignUpCommand("ana@correo.com", "Secreta123", Role.ADMINISTRADOR)));
        verify(cuentas, never()).save(any());
    }
}
