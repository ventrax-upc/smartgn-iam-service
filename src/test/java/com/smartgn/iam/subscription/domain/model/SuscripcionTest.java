package com.smartgn.iam.subscription.domain.model;

import com.smartgn.iam.auth.domain.model.Plan;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SuscripcionTest {

    @Test
    void planFreeMantieneMetricasYAlertasPrincipalesPeroNoLasFuncionesPro() {
        Suscripcion free = new Suscripcion(UUID.randomUUID(), Plan.FREE);

        assertTrue(free.permite(Feature.METRICAS_PRINCIPALES));
        assertTrue(free.permite(Feature.ALERTAS_PRINCIPALES));
        assertFalse(free.permite(Feature.FUNCIONES_PRO));
        assertEquals(Set.of(Feature.METRICAS_PRINCIPALES, Feature.ALERTAS_PRINCIPALES), free.funcionalidades());
    }

    @Test
    void planProHabilitaTodasLasFuncionalidades() {
        Suscripcion pro = new Suscripcion(UUID.randomUUID(), Plan.PRO);

        assertEquals(Set.of(Feature.values()), pro.funcionalidades());
    }
}
