package com.factura.facturacion.dtos.factura;

import java.math.BigDecimal;

public class FacturaPagoCrearDto {

    private String codigoFormaPagoSri; // 01, 19, 20, etc.
    private BigDecimal total;
    private Integer plazo;             // opcional
    private String unidadTiempo;       // dias, meses, etc.

    public String getCodigoFormaPagoSri() {
        return codigoFormaPagoSri;
    }

    public void setCodigoFormaPagoSri(String codigoFormaPagoSri) {
        this.codigoFormaPagoSri = codigoFormaPagoSri;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Integer getPlazo() {
        return plazo;
    }

    public void setPlazo(Integer plazo) {
        this.plazo = plazo;
    }

    public String getUnidadTiempo() {
        return unidadTiempo;
    }

    public void setUnidadTiempo(String unidadTiempo) {
        this.unidadTiempo = unidadTiempo;
    }
}
