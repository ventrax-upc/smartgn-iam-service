package com.smartgn.iam.subscription.interfaces.rest;

import com.smartgn.iam.auth.domain.model.Plan;
import com.smartgn.iam.subscription.domain.model.Feature;
import com.smartgn.iam.subscription.domain.model.Suscripcion;

import java.util.List;
import java.util.UUID;

public record SubscriptionResource(UUID accountId, Plan plan, List<Feature> features) {

    public static SubscriptionResource from(Suscripcion suscripcion) {
        return new SubscriptionResource(suscripcion.accountId(), suscripcion.plan(),
                List.copyOf(suscripcion.funcionalidades()));
    }
}
