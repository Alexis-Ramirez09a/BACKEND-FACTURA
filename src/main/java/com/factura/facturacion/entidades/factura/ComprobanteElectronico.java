package com.factura.facturacion.entidades.factura;

import java.time.LocalDateTime;

import com.factura.facturacion.entidades.base.EntidadAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comprobantes_electronicos")
public class ComprobanteElectronico extends EntidadAuditable {

    @OneToOne(optional = false)
    @JoinColumn(name = "factura_id")
    private Factura factura;

    @Column(name = "clave_acceso", length = 49, nullable = false)
    private String claveAcceso;

    @Column(name = "numero_autorizacion", length = 49)
    private String numeroAutorizacion;

    @Lob
    @Column(name = "xml_generado")
    private String xmlGenerado;

    @Lob
    @Column(name = "xml_autorizado")
    private String xmlAutorizado;

    @Column(name = "fecha_autorizacion")
    private LocalDateTime fechaAutorizacion;

    @Column(name = "estado_sri", length = 20)
    private String estadoSri; // RECIBIDA, AUTORIZADA, RECHAZADA

    @Column(length = 300)
    private String mensaje;

    // Getters y setters

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    public String getNumeroAutorizacion() {
        return numeroAutorizacion;
    }

    public void setNumeroAutorizacion(String numeroAutorizacion) {
        this.numeroAutorizacion = numeroAutorizacion;
    }

    public String getXmlGenerado() {
        return xmlGenerado;
    }

    public void setXmlGenerado(String xmlGenerado) {
        this.xmlGenerado = xmlGenerado;
    }

    public String getXmlAutorizado() {
        return xmlAutorizado;
    }

    public void setXmlAutorizado(String xmlAutorizado) {
        this.xmlAutorizado = xmlAutorizado;
    }

    public LocalDateTime getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(LocalDateTime fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    public String getEstadoSri() {
        return estadoSri;
    }

    public void setEstadoSri(String estadoSri) {
        this.estadoSri = estadoSri;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
