package com.smartgn.iam.auth.application.port.out;

import java.time.Instant;

public record PasswordResetNotification(String correo, String token, String enlace, Instant expiraEn) {
}
