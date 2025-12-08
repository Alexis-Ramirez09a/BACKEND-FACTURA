package com.factura.facturacion.entidades.empresa;

import com.factura.facturacion.entidades.base.EntidadAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresas")
public class Empresa extends EntidadAuditable {

    @Column(nullable = false, length = 13, unique = true)
    private String ruc;

    @Column(name = "razon_social", nullable = false, length = 200)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 200)
    private String nombreComercial;

    @Column(name = "direccion_matriz", nullable = false, length = 300)
    private String direccionMatriz;

    @Column(name = "contribuyente_especial", length = 20)
    private String contribuyenteEspecial;

    @Column(name = "obligado_llevar_contabilidad", length = 2)
    private String obligadoLlevarContabilidad; // "SI" / "NO"

    @Column(length = 20)
    private String telefono;

    @Column(name = "correo_notificacion", length = 150)
    private String correoNotificacion;

    @Column(length = 1)
    private String ambiente; // "1" pruebas, "2" producción

    @Column(name = "tipo_emision", length = 1)
    private String tipoEmision; // "1" normal

    @Column(name = "agente_retencion", length = 10)
    private String agenteRetencion; // Resolución Nro. 1

    @Column(name = "contribuyente_rimpe", length = 50)
    private String contribuyenteRimpe; // "CONTRIBUYENTE RÉGIMEN RIMPE"

    // Getters y setters
    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getDireccionMatriz() {
        return direccionMatriz;
    }

    public void setDireccionMatriz(String direccionMatriz) {
        this.direccionMatriz = direccionMatriz;
    }

    public String getContribuyenteEspecial() {
        return contribuyenteEspecial;
    }

    public void setContribuyenteEspecial(String contribuyenteEspecial) {
        this.contribuyenteEspecial = contribuyenteEspecial;
    }

    public String getObligadoLlevarContabilidad() {
        return obligadoLlevarContabilidad;
    }

    public void setObligadoLlevarContabilidad(String obligadoLlevarContabilidad) {
        this.obligadoLlevarContabilidad = obligadoLlevarContabilidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoNotificacion() {
        return correoNotificacion;
    }

    public void setCorreoNotificacion(String correoNotificacion) {
        this.correoNotificacion = correoNotificacion;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getTipoEmision() {
        return tipoEmision;
    }

    public void setTipoEmision(String tipoEmision) {
        this.tipoEmision = tipoEmision;
    }

    public String getAgenteRetencion() {
        return agenteRetencion;
    }

    public void setAgenteRetencion(String agenteRetencion) {
        this.agenteRetencion = agenteRetencion;
    }

    public String getContribuyenteRimpe() {
        return contribuyenteRimpe;
    }

    public void setContribuyenteRimpe(String contribuyenteRimpe) {
        this.contribuyenteRimpe = contribuyenteRimpe;
    }
}

// representa a la compañía que emite las facturas. Es decir, el emisor que está
// registrado ante el SRI. La clase Empresa representa al contribuyente que
// genera las facturas electrónicas. Contiene todos los campos que el SRI exige
// del emisor. Es la raíz del sistema: sin Empresa no hay facturación.//