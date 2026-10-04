package com.smartgn.iam.auth.application.port.in;

import com.smartgn.iam.auth.domain.model.Role;

public record SignUpCommand(String email, String password, Role role) {
}
