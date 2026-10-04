package com.smartgn.iam.auth.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** Password reset request. Only the token hash is stored, never the token itself. */
@Entity
@Table(name = "recuperacion_contrasena", schema = "iam")
public class RecuperacionContrasena {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_recuperacion")
    private UUID id;

    @Column(name = "id_cuenta", nullable = false, updatable = false)
    private UUID idCuenta;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "expira_en", nullable = false, updatable = false)
    private Instant expiraEn;

    @Column(name = "utilizado_en")
    private Instant utilizadoEn;

    protected RecuperacionContrasena() {
        // required by JPA
    }

    private RecuperacionContrasena(UUID idCuenta, String tokenHash, Instant creadoEn, Instant expiraEn) {
        this.idCuenta = idCuenta;
        this.tokenHash = tokenHash;
        this.creadoEn = creadoEn;
        this.expiraEn = expiraEn;
    }

    public static RecuperacionContrasena crear(UUID idCuenta, String tokenHash, Instant creadoEn, Instant expiraEn) {
        return new RecuperacionContrasena(idCuenta, tokenHash, creadoEn, expiraEn);
    }

    public boolean estaVigente(Instant ahora) {
        return utilizadoEn == null && ahora.isBefore(expiraEn);
    }

    public void marcarUtilizado(Instant ahora) {
        this.utilizadoEn = ahora;
    }

    public UUID getId() { return id; }
    public UUID getIdCuenta() { return idCuenta; }
    public String getTokenHash() { return tokenHash; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getExpiraEn() { return expiraEn; }
    public Instant getUtilizadoEn() { return utilizadoEn; }
}
