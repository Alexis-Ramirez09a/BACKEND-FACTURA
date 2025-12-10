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
}
// 📌 Producto = bienes o servicios que la empresa vende y factura.//