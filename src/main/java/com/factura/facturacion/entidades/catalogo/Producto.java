package com.factura.facturacion.entidades.catalogo;

import java.math.BigDecimal;

import com.factura.facturacion.entidades.base.EntidadAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto extends EntidadAuditable {

    @jakarta.validation.constraints.NotBlank(message = "El código principal es obligatorio")
    @Column(name = "codigo_principal", nullable = false, length = 50, unique = true)
    private String codigoPrincipal;

    @jakarta.validation.constraints.NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, length = 200)
    private String descripcion;

    @jakarta.validation.constraints.NotNull(message = "El precio unitario es obligatorio")
    @jakarta.validation.constraints.PositiveOrZero(message = "El precio no puede ser negativo")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 4)
    private BigDecimal precioUnitario;

    @jakarta.validation.constraints.NotNull(message = "La cantidad es obligatoria")
    @jakarta.validation.constraints.PositiveOrZero(message = "La cantidad no puede ser negativa")
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer cantidad = 0;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean activo = true;

    // Nuevo campo para guardar el ID del IVA en la tabla productos (como pidió el
    // usuario)
    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "tarifa_iva_id")
    private ImpuestoTarifa tarifaIva;

    // Getters y setters

    public String getCodigoPrincipal() {
        return codigoPrincipal;
    }

    public void setCodigoPrincipal(String codigoPrincipal) {
        this.codigoPrincipal = codigoPrincipal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @jakarta.persistence.Transient
    private String iva; // "12", "0", etc. (Solo para transporte de datos)

    @jakarta.persistence.OneToMany(mappedBy = "producto", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ProductoImpuesto> impuestos = new java.util.ArrayList<>();

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public java.util.List<ProductoImpuesto> getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(java.util.List<ProductoImpuesto> impuestos) {
        this.impuestos = impuestos;
    }

    public ImpuestoTarifa getTarifaIva() {
        return tarifaIva;
    }

    public void setTarifaIva(ImpuestoTarifa tarifaIva) {
        this.tarifaIva = tarifaIva;
    }
}
// 📌 Producto = bienes o servicios que la empresa vende y factura.//