package com.smartgn.iam.profile.application.service;

import com.smartgn.iam.profile.application.port.in.CreateProfileCommand;
import com.smartgn.iam.profile.application.port.in.Requester;
import com.smartgn.iam.profile.application.port.in.UpdateProfileCommand;
import com.smartgn.iam.profile.application.port.out.PerfilRepository;
import com.smartgn.iam.profile.domain.exception.AccesoAPerfilDenegadoException;
import com.smartgn.iam.profile.domain.exception.PerfilNoEncontradoException;
import com.smartgn.iam.profile.domain.exception.PerfilYaExisteException;
import com.smartgn.iam.profile.domain.model.Perfil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock PerfilRepository perfiles;
    @InjectMocks ProfileService service;

    private final UUID duenio = UUID.randomUUID();
    private final UUID otro = UUID.randomUUID();
    private final UUID profileId = UUID.randomUUID();
    private final Perfil perfil = Perfil.crear(duenio, "Ana", "Perez", "987654321", "Av. Lima 123");

    @Test
    void creaPerfilLimpiandoEspaciosYCamposOpcionales() {
        when(perfiles.existsByIdCuenta(duenio)).thenReturn(false);
        when(perfiles.save(any(Perfil.class))).thenAnswer(inv -> inv.getArgument(0));

        Perfil creado = service.create(new CreateProfileCommand(duenio, "  Ana ", "Perez", "  ", null));

        assertEquals(duenio, creado.getIdCuenta());
        assertEquals("Ana", creado.getNombres());
        assertNull(creado.getTelefono());
        assertNull(creado.getDireccion());
    }

    @Test
    void noPermiteSegundoPerfilParaLaMismaCuenta() {
        when(perfiles.existsByIdCuenta(duenio)).thenReturn(true);

        assertThrows(PerfilYaExisteException.class,
                () -> service.create(new CreateProfileCommand(duenio, "Ana", "Perez", null, null)));
        verify(perfiles, never()).save(any());
    }

    @Test
    void elDuenioPuedeConsultarSuPerfil() {
        when(perfiles.findById(profileId)).thenReturn(Optional.of(perfil));

        assertSame(perfil, service.getById(profileId, new Requester(duenio, false)));
    }

    @Test
    void otroUsuarioNoPuedeConsultarElPerfil() {
        when(perfiles.findById(profileId)).thenReturn(Optional.of(perfil));

        assertThrows(AccesoAPerfilDenegadoException.class,
                () -> service.getById(profileId, new Requester(otro, false)));
    }

    @Test
    void superAdminPuedeConsultarCualquierPerfil() {
        when(perfiles.findById(profileId)).thenReturn(Optional.of(perfil));

        assertSame(perfil, service.getById(profileId, new Requester(otro, true)));
    }

    @Test
    void consultaDePerfilInexistenteDaNoEncontrado() {
        when(perfiles.findById(profileId)).thenReturn(Optional.empty());

        assertThrows(PerfilNoEncontradoException.class,
                () -> service.getById(profileId, new Requester(duenio, false)));
    }

    @Test
    void elDuenioActualizaSusDatos() {
        when(perfiles.findById(profileId)).thenReturn(Optional.of(perfil));
        when(perfiles.save(any(Perfil.class))).thenAnswer(inv -> inv.getArgument(0));

        Perfil actualizado = service.update(
                new UpdateProfileCommand(profileId, duenio, "Ana Maria", "Perez", null, "Calle Sol 5"));

        assertEquals("Ana Maria", actualizado.getNombres());
        assertNull(actualizado.getTelefono());
        assertEquals("Calle Sol 5", actualizado.getDireccion());
    }

    @Test
    void otroUsuarioNoPuedeActualizarElPerfil() {
        when(perfiles.findById(profileId)).thenReturn(Optional.of(perfil));

        assertThrows(AccesoAPerfilDenegadoException.class,
                () -> service.update(new UpdateProfileCommand(profileId, otro, "X", "Y", null, null)));
        verify(perfiles, never()).save(any());
    }

    @Test
    void superAdminNoPuedeModificarPerfilesAjenos() {
        when(perfiles.findById(profileId)).thenReturn(Optional.of(perfil));

        assertThrows(AccesoAPerfilDenegadoException.class,
                () -> service.update(new UpdateProfileCommand(profileId, otro, "X", "Y", null, null)));
    }
}
