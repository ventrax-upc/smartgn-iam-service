package com.smartgn.iam.auth.application.port.in;

import com.smartgn.iam.auth.domain.model.Cuenta;

public interface SignUpUseCase {

    Cuenta execute(SignUpCommand command);
}
