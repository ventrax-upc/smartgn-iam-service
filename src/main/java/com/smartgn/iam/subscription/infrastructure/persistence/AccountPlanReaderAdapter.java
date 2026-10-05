package com.smartgn.iam.subscription.infrastructure.persistence;

import com.smartgn.iam.auth.application.port.out.CuentaRepository;
import com.smartgn.iam.auth.domain.model.Cuenta;
import com.smartgn.iam.auth.domain.model.Plan;
import com.smartgn.iam.subscription.application.port.out.PlanReader;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/** The plan lives on the account: it is read from the database, not the token, to avoid relying on a stale value. */
@Component
public class AccountPlanReaderAdapter implements PlanReader {

    private final CuentaRepository cuentas;

    public AccountPlanReaderAdapter(CuentaRepository cuentas) {
        this.cuentas = cuentas;
    }

    @Override
    public Optional<Plan> findPlanByAccountId(UUID accountId) {
        return cuentas.findById(accountId).map(Cuenta::getPlan);
    }
}
