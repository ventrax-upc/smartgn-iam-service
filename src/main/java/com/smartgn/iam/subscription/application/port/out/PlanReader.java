package com.smartgn.iam.subscription.application.port.out;

import com.smartgn.iam.auth.domain.model.Plan;

import java.util.Optional;
import java.util.UUID;

public interface PlanReader {

    Optional<Plan> findPlanByAccountId(UUID accountId);
}
