package com.smartgn.iam.auth.application.port.in;

import com.smartgn.iam.auth.domain.model.Cuenta;

public record SignInResult(Cuenta cuenta, String token) {
}
