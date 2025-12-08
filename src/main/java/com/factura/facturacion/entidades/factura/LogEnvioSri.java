package com.factura.facturacion.entidades.factura;

import java.time.LocalDateTime;

import com.factura.facturacion.entidades.base.EntidadAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "log_envio_sri")
public class LogEnvioSri extends EntidadAuditable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "factura_id")
    private Factura factura;

    @Column(name = "estado", length = 20)
    private String estado; // RECIBIDA, AUTORIZADA, RECHAZADA, ERROR

    @Lob
    @Column(name = "xml_enviado")
    private String xmlEnviado;

    @Lob
    @Column(name = "xml_respuesta")
    private String xmlRespuesta;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(length = 300)
    private String mensaje;

    // Getters y setters

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getXmlEnviado() {
        return xmlEnviado;
    }

    public void setXmlEnviado(String xmlEnviado) {
        this.xmlEnviado = xmlEnviado;
    }

    public String getXmlRespuesta() {
        return xmlRespuesta;
    }

    public void setXmlRespuesta(String xmlRespuesta) {
        this.xmlRespuesta = xmlRespuesta;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}