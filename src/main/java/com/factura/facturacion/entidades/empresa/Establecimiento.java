package com.factura.facturacion.entidades.empresa;

import com.factura.facturacion.entidades.base.EntidadAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "establecimientos")
public class Establecimiento extends EntidadAuditable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @Column(nullable = false, length = 3)
    private String codigo; // 001, 002, etc.

    @Column(nullable = false, length = 300)
    private String direccion;

    @Column(length = 100)
    private String descripcion;

    @Column(length = 20)
    private String telefono;

    // Getters y setters

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}

//📌 Establecimiento = Sucursal registrada en el SRI.//
//📌 Forma parte de la numeración del comprobante.//
//📌 Una empresa puede tener muchos establecimientos.//
//📌 Es obligatorio para emitir facturas electrónicas válidas.//