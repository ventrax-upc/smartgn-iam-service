package com.smartgn.iam.profile.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "perfil", schema = "iam")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_perfil")
    private UUID id;

    @Column(name = "id_cuenta", nullable = false, unique = true, updatable = false)
    private UUID idCuenta;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "direccion")
    private String direccion;

    protected Perfil() {
        // required by JPA
    }

    private Perfil(UUID idCuenta, String nombres, String apellidos, String telefono, String direccion) {
        this.idCuenta = idCuenta;
        asignar(nombres, apellidos, telefono, direccion);
    }

    public static Perfil crear(UUID idCuenta, String nombres, String apellidos, String telefono, String direccion) {
        return new Perfil(idCuenta, nombres, apellidos, telefono, direccion);
    }

    /** Full replacement: first and last names are required; phone and address are optional. */
    public void actualizar(String nombres, String apellidos, String telefono, String direccion) {
        asignar(nombres, apellidos, telefono, direccion);
    }

    public boolean perteneceA(UUID idCuenta) {
        return this.idCuenta.equals(idCuenta);
    }

    private void asignar(String nombres, String apellidos, String telefono, String direccion) {
        this.nombres = requerido(nombres, "first name");
        this.apellidos = requerido(apellidos, "last name");
        this.telefono = opcional(telefono);
        this.direccion = opcional(direccion);
    }

    private static String requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " is required");
        }
        return valor.trim();
    }

    private static String opcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }

    public UUID getId() { return id; }
    public UUID getIdCuenta() { return idCuenta; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
}
