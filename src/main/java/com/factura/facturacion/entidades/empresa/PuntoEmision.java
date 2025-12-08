package com.factura.facturacion.entidades.empresa;

import com.factura.facturacion.entidades.base.EntidadAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "puntos_emision")
public class PuntoEmision extends EntidadAuditable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "establecimiento_id")
    private Establecimiento establecimiento;

    @Column(nullable = false, length = 3)
    private String codigo; // 001, 002, etc.

    @Column(length = 100)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    // Getters y setters

    public Establecimiento getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(Establecimiento establecimiento) {
        this.establecimiento = establecimiento;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}

//📌 PuntoEmision = Caja o sistema desde donde se emite la factura.
//📌 Forma parte obligatoria del código de comprobante.
//📌 Un establecimiento puede tener varios puntos de emisión.
//📌 Necesario para cumplir con la estructura del SRI.