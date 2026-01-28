package com.factura.facturacion.entidades.factura;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.factura.facturacion.entidades.base.EntidadAuditable;
import com.factura.facturacion.entidades.catalogo.Cliente;
import com.factura.facturacion.entidades.empresa.Empresa;
import com.factura.facturacion.entidades.empresa.Establecimiento;
import com.factura.facturacion.entidades.empresa.PuntoEmision;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "facturas")
public class Factura extends EntidadAuditable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToOne(optional = false)
    @JoinColumn(name = "establecimiento_id")
    private Establecimiento establecimiento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "punto_emision_id")
    private PuntoEmision puntoEmision;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(optional = true) // Optional initially to avoid breaking existing data immediately, or force
                                // false if we migrate
    @JoinColumn(name = "usuario_id")
    private com.factura.facturacion.entidades.seguridad.Usuario usuario;

    @Column(name = "tipo_comprobante", nullable = false, length = 2)
    private String tipoComprobante = "01"; // 01 = factura

    @Column(name = "codigo_establecimiento", nullable = false, length = 3)
    private String codigoEstablecimiento; // 001

    @Column(name = "codigo_punto_emision", nullable = false, length = 3)
    private String codigoPuntoEmision; // 001

    @Column(nullable = false, length = 9)
    private String secuencial; // 000000123

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    // Datos del comprador
    @Column(name = "tipo_identificacion_comprador", nullable = false, length = 2)
    private String tipoIdentificacionComprador;

    @Column(name = "identificacion_comprador", nullable = false, length = 20)
    private String identificacionComprador;

    @Column(name = "razon_social_comprador", nullable = false, length = 200)
    private String razonSocialComprador;

    @Column(name = "direccion_comprador", length = 300)
    private String direccionComprador;

    // Totales
    @Column(name = "total_sin_impuestos", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSinImpuestos = BigDecimal.ZERO;

    @Column(name = "total_descuento", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDescuento = BigDecimal.ZERO;

    @Column(name = "subtotal_iva_12", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalIva12 = BigDecimal.ZERO;

    @Column(name = "subtotal_iva_15", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalIva15 = BigDecimal.ZERO;

    @Column(name = "subtotal_iva_0", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalIva0 = BigDecimal.ZERO;

    @Column(name = "subtotal_no_objeto_iva", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalNoObjetoIva = BigDecimal.ZERO;

    @Column(name = "subtotal_exento_iva", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalExentoIva = BigDecimal.ZERO;

    @Column(name = "valor_iva", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorIva = BigDecimal.ZERO;

    @Column(name = "valor_ice", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorIce = BigDecimal.ZERO;

    @Column(name = "valor_irbpnr", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorIrbpnr = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal propina = BigDecimal.ZERO;

    @Column(name = "importe_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal importeTotal = BigDecimal.ZERO;

    @Column(length = 10)
    private String moneda = "DOLAR";

    @Column(length = 20, nullable = false)
    private String estado = "PENDIENTE"; // PENDIENTE, AUTORIZADA, RECHAZADA, ANULADA

    @Column(length = 300)
    private String observacion;

    @Column(name = "clave_acceso", length = 49)
    private String claveAcceso;

    @Column(name = "guia_remision", length = 20)
    private String guiaRemision;

    @Column(name = "fecha_autorizacion")
    private java.time.LocalDateTime fechaAutorizacion;

    @Column(name = "mensaje_error", length = 500)
    private String mensajeError;

    // Relación con detalles de la factura
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaDetalle> detalles = new ArrayList<>();

    // Relación con pagos
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaPago> pagos = new ArrayList<>();

    // Getters y setters

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public com.factura.facturacion.entidades.seguridad.Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(com.factura.facturacion.entidades.seguridad.Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getCodigoEstablecimiento() {
        return codigoEstablecimiento;
    }

    public void setCodigoEstablecimiento(String codigoEstablecimiento) {
        this.codigoEstablecimiento = codigoEstablecimiento;
    }

    public String getCodigoPuntoEmision() {
        return codigoPuntoEmision;
    }

    public void setCodigoPuntoEmision(String codigoPuntoEmision) {
        this.codigoPuntoEmision = codigoPuntoEmision;
    }

    public String getSecuencial() {
        return secuencial;
    }

    public void setSecuencial(String secuencial) {
        this.secuencial = secuencial;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getTipoIdentificacionComprador() {
        return tipoIdentificacionComprador;
    }

    public void setTipoIdentificacionComprador(String tipoIdentificacionComprador) {
        this.tipoIdentificacionComprador = tipoIdentificacionComprador;
    }

    public String getIdentificacionComprador() {
        return identificacionComprador;
    }

    public void setIdentificacionComprador(String identificacionComprador) {
        this.identificacionComprador = identificacionComprador;
    }

    public String getRazonSocialComprador() {
        return razonSocialComprador;
    }

    public void setRazonSocialComprador(String razonSocialComprador) {
        this.razonSocialComprador = razonSocialComprador;
    }

    public String getDireccionComprador() {
        return direccionComprador;
    }

    public void setDireccionComprador(String direccionComprador) {
        this.direccionComprador = direccionComprador;
    }

    public BigDecimal getTotalSinImpuestos() {
        return totalSinImpuestos;
    }

    public void setTotalSinImpuestos(BigDecimal totalSinImpuestos) {
        this.totalSinImpuestos = totalSinImpuestos;
    }

    public BigDecimal getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(BigDecimal totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public BigDecimal getSubtotalIva12() {
        return subtotalIva12;
    }

    public void setSubtotalIva12(BigDecimal subtotalIva12) {
        this.subtotalIva12 = subtotalIva12;
    }

    public BigDecimal getSubtotalIva15() {
        return subtotalIva15;
    }

    public void setSubtotalIva15(BigDecimal subtotalIva15) {
        this.subtotalIva15 = subtotalIva15;
    }

    public BigDecimal getSubtotalIva0() {
        return subtotalIva0;
    }

    public void setSubtotalIva0(BigDecimal subtotalIva0) {
        this.subtotalIva0 = subtotalIva0;
    }

    public BigDecimal getSubtotalNoObjetoIva() {
        return subtotalNoObjetoIva;
    }

    public void setSubtotalNoObjetoIva(BigDecimal subtotalNoObjetoIva) {
        this.subtotalNoObjetoIva = subtotalNoObjetoIva;
    }

    public BigDecimal getSubtotalExentoIva() {
        return subtotalExentoIva;
    }

    public void setSubtotalExentoIva(BigDecimal subtotalExentoIva) {
        this.subtotalExentoIva = subtotalExentoIva;
    }

    public BigDecimal getValorIva() {
        return valorIva;
    }

    public void setValorIva(BigDecimal valorIva) {
        this.valorIva = valorIva;
    }

    public BigDecimal getValorIce() {
        return valorIce;
    }

    public void setValorIce(BigDecimal valorIce) {
        this.valorIce = valorIce;
    }

    public BigDecimal getValorIrbpnr() {
        return valorIrbpnr;
    }

    public void setValorIrbpnr(BigDecimal valorIrbpnr) {
        this.valorIrbpnr = valorIrbpnr;
    }

    public BigDecimal getPropina() {
        return propina;
    }

    public void setPropina(BigDecimal propina) {
        this.propina = propina;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    public String getGuiaRemision() {
        return guiaRemision;
    }

    public void setGuiaRemision(String guiaRemision) {
        this.guiaRemision = guiaRemision;
    }

    public java.time.LocalDateTime getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(java.time.LocalDateTime fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public List<FacturaDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<FacturaDetalle> detalles) {
        this.detalles = detalles;
    }

    public List<FacturaPago> getPagos() {
        return pagos;
    }

    public void setPagos(List<FacturaPago> pagos) {
        this.pagos = pagos;
    }

    // Adaptación para Frontend (JSON)
    public BigDecimal getTotal() {
        return this.importeTotal;
    }

    public String getClienteNombre() {
        return this.cliente != null ? this.cliente.getNombreRazonSocial() : "";
    }
}
