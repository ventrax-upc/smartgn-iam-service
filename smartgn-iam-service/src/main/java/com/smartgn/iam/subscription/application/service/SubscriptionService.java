package com.smartgn.iam.subscription.application.service;

import com.smartgn.iam.subscription.application.port.in.GetSubscriptionUseCase;
import com.smartgn.iam.subscription.application.port.out.PlanReader;
import com.smartgn.iam.subscription.domain.exception.SuscripcionNoEncontradaException;
import com.smartgn.iam.subscription.domain.model.Suscripcion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SubscriptionService implements GetSubscriptionUseCase {

    private final PlanReader planReader;

    public SubscriptionService(PlanReader planReader) {
        this.planReader = planReader;
    }

    @Override
    @Transactional(readOnly = true)
    public Suscripcion getByAccountId(UUID accountId) {
        return planReader.findPlanByAccountId(accountId)
                .map(plan -> new Suscripcion(accountId, plan))
                .orElseThrow(SuscripcionNoEncontradaException::new);
    }
}
