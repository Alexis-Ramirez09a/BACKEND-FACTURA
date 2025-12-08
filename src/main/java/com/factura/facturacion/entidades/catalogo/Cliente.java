package com.factura.facturacion.entidades.catalogo;

import com.factura.facturacion.entidades.base.EntidadAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente extends EntidadAuditable {

    @jakarta.validation.constraints.NotBlank(message = "La identificación es obligatoria")
    @jakarta.validation.constraints.Size(min = 10, max = 13, message = "La identificación debe tener entre 10 y 13 caracteres")
    @Column(nullable = false, length = 13, unique = true)
    private String identificacion; // cédula, RUC o pasaporte

    @Column(name = "tipo_identificacion", nullable = false, length = 2)
    private String tipoIdentificacion; // 04=RUC, 05=Cédula, 06=Pasaporte

    @jakarta.validation.constraints.NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre_razon_social", nullable = false, length = 200)
    private String nombreRazonSocial;

    @Column(length = 300)
    private String direccion;

    @Column(length = 20)
    private String telefono;

    @jakarta.validation.constraints.Email(message = "El correo debe ser válido")
    @Column(length = 150)
    private String correo;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean activo = true;

    // Getters y setters

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getNombreRazonSocial() {
        return nombreRazonSocial;
    }

    public void setNombreRazonSocial(String nombreRazonSocial) {
        this.nombreRazonSocial = nombreRazonSocial;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
