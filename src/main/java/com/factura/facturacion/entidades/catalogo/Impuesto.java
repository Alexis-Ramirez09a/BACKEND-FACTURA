package com.factura.facturacion.entidades.catalogo;

import com.factura.facturacion.entidades.base.EntidadAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "impuestos")
public class Impuesto extends EntidadAuditable {

    @Column(nullable = false, length = 2)
    private String codigo; // 2 = IVA, 3 = ICE, 5 = IRBPNR

    @Column(nullable = false, length = 100)
    private String nombre; // IVA, ICE, etc.

    @Column(length = 300)
    private String descripcion;

    // Getters y setters

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}