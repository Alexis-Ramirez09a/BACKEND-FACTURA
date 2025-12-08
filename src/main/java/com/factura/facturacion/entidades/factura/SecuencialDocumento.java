package com.factura.facturacion.entidades.factura;

import com.factura.facturacion.entidades.base.EntidadAuditable;
import com.factura.facturacion.entidades.empresa.Establecimiento;
import com.factura.facturacion.entidades.empresa.PuntoEmision;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "secuenciales_documento")
public class SecuencialDocumento extends EntidadAuditable {

    @Column(name = "tipo_comprobante", nullable = false, length = 2)
    private String tipoComprobante; // 01 factura, 04 nota de crédito, etc.

    @ManyToOne(optional = false)
    @JoinColumn(name = "establecimiento_id")
    private Establecimiento establecimiento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "punto_emision_id")
    private PuntoEmision puntoEmision;

    @Column(name = "ultimo_secuencial", nullable = false)
    private Integer ultimoSecuencial;

    @Column(nullable = false)
    private Boolean activo = true;

    // Getters y setters

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public Establecimiento getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(Establecimiento establecimiento) {
        this.establecimiento = establecimiento;
    }

    public PuntoEmision getPuntoEmision() {
        return puntoEmision;
    }

    public void setPuntoEmision(PuntoEmision puntoEmision) {
        this.puntoEmision = puntoEmision;
    }

    public Integer getUltimoSecuencial() {
        return ultimoSecuencial;
    }

    public void setUltimoSecuencial(Integer ultimoSecuencial) {
        this.ultimoSecuencial = ultimoSecuencial;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}

//📌 Esta clase controla el próximo número de factura o comprobante.
//📌 Está ligada a establecimiento + punto de emisión + tipo de comprobante.
//📌 Es obligatoria para cumplir con el SRI.
//📌 Es la base para formar números como: