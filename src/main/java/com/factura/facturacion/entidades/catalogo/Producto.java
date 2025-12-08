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

    @Column(name = "codigo_auxiliar", length = 50)
    private String codigoAuxiliar;

    @jakarta.validation.constraints.NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, length = 200)
    private String descripcion;

    @jakarta.validation.constraints.NotNull(message = "El precio unitario es obligatorio")
    @jakarta.validation.constraints.PositiveOrZero(message = "El precio no puede ser negativo")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 4)
    private BigDecimal precioUnitario;

    @Column(name = "unidad_medida", length = 10)
    private String unidadMedida; // UND, KG, LT, etc.

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean activo = true;

    // Getters y setters

    public String getCodigoPrincipal() {
        return codigoPrincipal;
    }

    public void setCodigoPrincipal(String codigoPrincipal) {
        this.codigoPrincipal = codigoPrincipal;
    }

    public String getCodigoAuxiliar() {
        return codigoAuxiliar;
    }

    public void setCodigoAuxiliar(String codigoAuxiliar) {
        this.codigoAuxiliar = codigoAuxiliar;
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

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
// 📌 Producto = bienes o servicios que la empresa vende y factura.//