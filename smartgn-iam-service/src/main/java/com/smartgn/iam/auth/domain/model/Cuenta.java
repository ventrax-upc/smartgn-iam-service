package com.smartgn.iam.auth.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cuenta", schema = "iam")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cuenta")
    private UUID id;

    @Column(name = "correo", nullable = false, unique = true, length = 254)
    private String correo;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private Role rol;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false, length = 10)
    private Plan plan;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private Instant fechaRegistro;

    protected Cuenta() {
        // required by JPA
    }

    private Cuenta(String correo, String passwordHash, Role rol) {
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.plan = Plan.FREE;
        this.fechaRegistro = Instant.now();
    }

    /** Every new account starts on the FREE plan. The public-role restriction lives in the sign-up use case. */
    public static Cuenta registrar(String correo, String passwordHash, Role rol) {
        return new Cuenta(correo, passwordHash, rol);
    }

    public void cambiarPasswordHash(String nuevoHash) {
        this.passwordHash = nuevoHash;
    }

    public UUID getId() { return id; }
    public String getCorreo() { return correo; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRol() { return rol; }
    public Plan getPlan() { return plan; }
    public Instant getFechaRegistro() { return fechaRegistro; }
}
