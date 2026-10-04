package com.smartgn.iam.subscription.application.service;

import com.smartgn.iam.auth.domain.model.Plan;
import com.smartgn.iam.subscription.application.port.out.PlanReader;
import com.smartgn.iam.subscription.domain.exception.SuscripcionNoEncontradaException;
import com.smartgn.iam.subscription.domain.model.Suscripcion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock PlanReader planReader;
    @InjectMocks SubscriptionService service;

    private final UUID accountId = UUID.randomUUID();

    @Test
    void devuelveElPlanActualDeLaCuenta() {
        when(planReader.findPlanByAccountId(accountId)).thenReturn(Optional.of(Plan.PRO));

        Suscripcion suscripcion = service.getByAccountId(accountId);

        assertEquals(Plan.PRO, suscripcion.plan());
        assertEquals(accountId, suscripcion.accountId());
    }

    @Test
    void fallaSiLaCuentaNoExiste() {
        when(planReader.findPlanByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(SuscripcionNoEncontradaException.class, () -> service.getByAccountId(accountId));
    }
}
