package com.smartgn.iam.auth.infrastructure.security;

import com.smartgn.iam.auth.application.port.out.TokenClaims;
import com.smartgn.iam.auth.application.port.out.TokenProvider;
import com.smartgn.iam.auth.domain.model.Cuenta;
import com.smartgn.iam.auth.domain.model.Plan;
import com.smartgn.iam.auth.domain.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtProvider implements TokenProvider {

    static final String ISSUER = "smartgn-iam-service";

    private final SecretKey key;
    private final Duration expiration;

    public JwtProvider(@Value("${smartgn.security.jwt.secret}") String secret,
                       @Value("${smartgn.security.jwt.expiration-minutes}") long expirationMinutes) {
        // Fails at startup if the key is shorter than 256 bits (32 characters)
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    @Override
    public String generarToken(Cuenta cuenta) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(cuenta.getId().toString())
                .claim("accountId", cuenta.getId().toString())
                .claim("email", cuenta.getCorreo())
                .claim("role", cuenta.getRol().name())
                .claim("plan", cuenta.getPlan().name())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(expiration)))
                .signWith(key)
                .compact();
    }

    @Override
    public Optional<TokenClaims> validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(new TokenClaims(
                    UUID.fromString(claims.get("accountId", String.class)),
                    claims.get("email", String.class),
                    Role.valueOf(claims.get("role", String.class)),
                    Plan.valueOf(claims.get("plan", String.class))));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
