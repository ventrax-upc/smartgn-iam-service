package com.smartgn.iam.auth.infrastructure.notification;

import com.smartgn.iam.auth.application.port.out.PasswordResetNotification;
import com.smartgn.iam.auth.application.port.out.PasswordResetNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Placeholder outside the local profile, until the external email system is connected.
 * It sends nothing and, for security, does NOT log the token or the link.
 * Replace it with a real adapter that implements PasswordResetNotifier.
 */
@Component
@Profile("!local")
public class PendingExternalPasswordResetNotifier implements PasswordResetNotifier {

    private static final Logger log = LoggerFactory.getLogger(PendingExternalPasswordResetNotifier.class);

    @Override
    public void enviar(PasswordResetNotification notificacion) {
        log.warn("Password recovery requested, but the external email system is not connected yet. "
                + "The user was not notified.");
    }
}
