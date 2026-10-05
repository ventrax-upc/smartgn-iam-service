package com.smartgn.iam.subscription.application.port.in;

import com.smartgn.iam.subscription.domain.model.Suscripcion;

import java.util.UUID;

public interface GetSubscriptionUseCase {

    Suscripcion getByAccountId(UUID accountId);
}
