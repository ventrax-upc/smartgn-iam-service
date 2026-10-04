package com.smartgn.iam.auth.infrastructure.notification;

import com.smartgn.iam.auth.application.port.out.PasswordResetNotification;
import com.smartgn.iam.auth.application.port.out.PasswordResetNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Local development only: prints the link to the console so the flow can be tested without the external system. */
@Component
@Profile("local")
public class LocalLoggingPasswordResetNotifier implements PasswordResetNotifier {

    private static final Logger log = LoggerFactory.getLogger(LocalLoggingPasswordResetNotifier.class);

    @Override
    public void enviar(PasswordResetNotification notificacion) {
        log.info("[LOCAL] Password recovery link for {} (expires {}): {}",
                notificacion.correo(), notificacion.expiraEn(), notificacion.enlace());
    }
}
