package com.smartgn.iam.subscription.domain.model;

import com.smartgn.iam.auth.domain.model.Plan;

/**
 * Platform features and the minimum plan that enables each one.
 * Plan is ordered from lowest to highest (FREE < PRO), so comparing is enough.
 * Core metrics and alerts are always available on FREE (RF06).
 */
public enum Feature {
    METRICAS_PRINCIPALES(Plan.FREE),
    ALERTAS_PRINCIPALES(Plan.FREE),
    // TODO: replace with the real Pro-only features defined by the team
    FUNCIONES_PRO(Plan.PRO);

    private final Plan planMinimo;

    Feature(Plan planMinimo) {
        this.planMinimo = planMinimo;
    }

    public boolean habilitadaPara(Plan plan) {
        return plan.compareTo(planMinimo) >= 0;
    }
}
