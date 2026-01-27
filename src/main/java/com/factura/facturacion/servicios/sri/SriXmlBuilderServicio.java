package com.factura.facturacion.servicios.sri;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.entidades.factura.FacturaDetalle;
import com.factura.facturacion.entidades.factura.FacturaDetalleImpuesto;
import com.factura.facturacion.entidades.factura.FacturaPago;

@Service
public class SriXmlBuilderServicio {

  // Formato fecha SRI
  private static final DateTimeFormatter SRI_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  public String construirXmlFactura(Factura factura) {

    String fechaEmision = factura.getFechaEmision().format(SRI_DATE_FORMAT);

    // 1. Detalles
    StringBuilder detallesXml = new StringBuilder();

    for (FacturaDetalle det : factura.getDetalles()) {
      BigDecimal precioUnitario = det.getPrecioUnitario();
      BigDecimal cantidad = det.getCantidad();
      BigDecimal descuento = det.getDescuento() != null ? det.getDescuento() : BigDecimal.ZERO;
      BigDecimal precioTotalSinImp = det.getPrecioTotalSinImpuesto();

      StringBuilder impuestosXml = new StringBuilder();
      for (FacturaDetalleImpuesto imp : det.getImpuestos()) {
        impuestosXml.append("""
            <impuesto>
                <codigo>%s</codigo>
                <codigoPorcentaje>%s</codigoPorcentaje>
                <tarifa>%s</tarifa>
                <baseImponible>%s</baseImponible>
                <valor>%s</valor>
            </impuesto>
            """.formatted(
            imp.getCodigoImpuesto(),
            imp.getCodigoPorcentaje(),
            imp.getImpuestoTarifa().getPorcentaje().toPlainString(), // Tarifa (porcentaje)
            imp.getBaseImponible().toPlainString(),
            imp.getValor().toPlainString()));
      }

      detallesXml.append("""
          <detalle>
              <codigoPrincipal>%s</codigoPrincipal>
              <descripcion>%s</descripcion>
              <cantidad>%s</cantidad>
              <precioUnitario>%s</precioUnitario>
              <descuento>%s</descuento>
              <precioTotalSinImpuesto>%s</precioTotalSinImpuesto>
              <impuestos>
                  %s
              </impuestos>
          </detalle>
          """.formatted(
          det.getCodigoPrincipal(),
          det.getDescripcion(),
          cantidad.toPlainString(),
          precioUnitario.toPlainString(),
          descuento.toPlainString(),
          precioTotalSinImp.toPlainString(),
          impuestosXml.toString()));
    }

    // 2. Pagos
    StringBuilder pagosXml = new StringBuilder();
    if (factura.getPagos() != null && !factura.getPagos().isEmpty()) {
      pagosXml.append("<pagos>");
      for (FacturaPago pago : factura.getPagos()) {
        pagosXml.append("""
            <pago>
                <formaPago>%s</formaPago>
                <total>%s</total>
                <plazo>%s</plazo>
                <unidadTiempo>%s</unidadTiempo>
            </pago>
            """.formatted(
            pago.getFormaPago().getCodigoSri(),
            pago.getTotal().toPlainString(),
            pago.getPlazo(),
            pago.getUnidadTiempo()));
      }
      pagosXml.append("</pagos>");
    }

    // 3. Info Adicional
    StringBuilder infoAdicionalXml = new StringBuilder();
    if (factura.getCliente().getCorreo() != null || factura.getObservacion() != null) {
      infoAdicionalXml.append("<infoAdicional>");
      if (factura.getCliente().getCorreo() != null) {
        infoAdicionalXml
            .append("<campoAdicional nombre=\"Email\">%s</campoAdicional>".formatted(factura.getCliente().getCorreo()));
      }
      if (factura.getObservacion() != null) {
        infoAdicionalXml
            .append("<campoAdicional nombre=\"Observacion\">%s</campoAdicional>".formatted(factura.getObservacion()));
      }
      infoAdicionalXml.append("</infoAdicional>");
    }

    // 4. Total Con Impuestos (Resumen)
    // Por simplicidad, asumimos que ya tenemos los totales en la factura,
    // pero para el XML se suele pedir agrupado por código de impuesto.
    // Aquí simplificaremos mostrando el total de IVA si existe.
    StringBuilder totalConImpuestosXml = new StringBuilder();
    totalConImpuestosXml.append("<totalConImpuestos>");

    // IVA 12%
    if (factura.getSubtotalIva12().compareTo(BigDecimal.ZERO) > 0) {
      totalConImpuestosXml.append("""
          <totalImpuesto>
              <codigo>2</codigo>
              <codigoPorcentaje>2</codigoPorcentaje>
              <baseImponible>%s</baseImponible>
              <valor>%s</valor>
          </totalImpuesto>
          """.formatted(factura.getSubtotalIva12().toPlainString(), factura.getValorIva().toPlainString()));
    }
    // IVA 0%
    if (factura.getSubtotalIva0().compareTo(BigDecimal.ZERO) > 0) {
      totalConImpuestosXml.append("""
          <totalImpuesto>
              <codigo>2</codigo>
              <codigoPorcentaje>0</codigoPorcentaje>
              <baseImponible>%s</baseImponible>
              <valor>0.00</valor>
          </totalImpuesto>
          """.formatted(factura.getSubtotalIva0().toPlainString()));
    }
    // No Objeto
    if (factura.getSubtotalNoObjetoIva().compareTo(BigDecimal.ZERO) > 0) {
      totalConImpuestosXml.append("""
          <totalImpuesto>
              <codigo>2</codigo>
              <codigoPorcentaje>6</codigoPorcentaje>
              <baseImponible>%s</baseImponible>
              <valor>0.00</valor>
          </totalImpuesto>
          """.formatted(factura.getSubtotalNoObjetoIva().toPlainString()));
    }
    // Exento
    if (factura.getSubtotalExentoIva().compareTo(BigDecimal.ZERO) > 0) {
      totalConImpuestosXml.append("""
          <totalImpuesto>
              <codigo>2</codigo>
              <codigoPorcentaje>7</codigoPorcentaje>
              <baseImponible>%s</baseImponible>
              <valor>0.00</valor>
          </totalImpuesto>
          """.formatted(factura.getSubtotalExentoIva().toPlainString()));
    }

    totalConImpuestosXml.append("</totalConImpuestos>");

    // Campos opcionales de Empresa
    String agenteRetencionTag = "";
    if (factura.getEmpresa().getAgenteRetencion() != null && !factura.getEmpresa().getAgenteRetencion().isEmpty()) {
      agenteRetencionTag = "<agenteRetencion>%s</agenteRetencion>".formatted(factura.getEmpresa().getAgenteRetencion());
    }

    String contribuyenteRimpeTag = "";
    if (factura.getEmpresa().getContribuyenteRimpe() != null
        && !factura.getEmpresa().getContribuyenteRimpe().isEmpty()) {
      contribuyenteRimpeTag = "<contribuyenteRimpe>%s</contribuyenteRimpe>"
          .formatted(factura.getEmpresa().getContribuyenteRimpe());
    }

    String guiaRemisionTag = "";
    if (factura.getGuiaRemision() != null && !factura.getGuiaRemision().isEmpty()) {
      guiaRemisionTag = "<guiaRemision>%s</guiaRemision>".formatted(factura.getGuiaRemision());
    }

    // Sanitize Client Data (Fix for potential data corruption)
    String tipoIdentificacion = factura.getCliente().getTipoIdentificacion();
    String razonSocial = factura.getCliente().getNombreRazonSocial();
    String identificacion = factura.getCliente().getIdentificacion();

    // Heurística de corrección: Si el tipo es "NO" o inválido
    if (tipoIdentificacion == null || tipoIdentificacion.length() != 2 || "NO".equals(tipoIdentificacion)) {
      // Si la Razón Social parece un código (ej. "05") y la Identificación parece un
      // nombre
      if (razonSocial != null && razonSocial.matches("\\d{2}") && identificacion != null
          && !identificacion.matches("\\d+")) {
        tipoIdentificacion = razonSocial; // Recuperamos el "05"
        razonSocial = identificacion; // Recuperamos el nombre "Alexis..."
        identificacion = "9999999999999"; // ID perdido, usamos CF
      } else {
        tipoIdentificacion = "07"; // Por defecto Consumidor Final
        identificacion = "9999999999999";
        if (razonSocial == null || razonSocial.isEmpty())
          razonSocial = "CONSUMIDOR FINAL";
      }
    }

    String xml = """
        <factura id="comprobante" version="1.1.0">
          <infoTributaria>
            <ambiente>%s</ambiente>
            <tipoEmision>%s</tipoEmision>
            <razonSocial>%s</razonSocial>
            <nombreComercial>%s</nombreComercial>
            <ruc>%s</ruc>
            <claveAcceso>%s</claveAcceso>
            <codDoc>01</codDoc>
            <estab>%s</estab>
            <ptoEmi>%s</ptoEmi>
            <secuencial>%s</secuencial>
            <dirMatriz>%s</dirMatriz>
            %s
            %s
          </infoTributaria>

          <infoFactura>
            <fechaEmision>%s</fechaEmision>
            <dirEstablecimiento>%s</dirEstablecimiento>
            %s
            <obligadoContabilidad>%s</obligadoContabilidad>
            <tipoIdentificacionComprador>%s</tipoIdentificacionComprador>
            %s
            <razonSocialComprador>%s</razonSocialComprador>
            <identificacionComprador>%s</identificacionComprador>
            <totalSinImpuestos>%s</totalSinImpuestos>
            <totalDescuento>%s</totalDescuento>
            %s
            <propina>%s</propina>
            <importeTotal>%s</importeTotal>
            <moneda>DOLAR</moneda>
            %s
          </infoFactura>

          <detalles>
            %s
          </detalles>

          %s

        </factura>
        """.formatted(
        // infoTributaria
        factura.getEmpresa().getAmbiente(),
        factura.getEmpresa().getTipoEmision(),
        factura.getEmpresa().getRazonSocial(),
        factura.getEmpresa().getNombreComercial(),
        factura.getEmpresa().getRuc(),
        factura.getClaveAcceso(),
        factura.getEstablecimiento().getCodigo(),
        factura.getPuntoEmision().getCodigo(),
        factura.getSecuencial(),
        factura.getEmpresa().getDireccionMatriz(),
        agenteRetencionTag,
        contribuyenteRimpeTag,

        // infoFactura
        fechaEmision,
        factura.getEstablecimiento().getDireccion(),
        // Contribuyente Especial (si existe)
        (factura.getEmpresa().getContribuyenteEspecial() != null
            && !factura.getEmpresa().getContribuyenteEspecial().isEmpty())
                ? "<contribuyenteEspecial>" + factura.getEmpresa().getContribuyenteEspecial()
                    + "</contribuyenteEspecial>"
                : "",
        // Obligado a llevar contabilidad (OBLIGATORIO)
        // EL USUARIO SOLICITÓ CAMBIAR A 'SI' SIEMPRE
        "SI",
        tipoIdentificacion,
        // Guía de Remisión (si existe)
        guiaRemisionTag,
        razonSocial,
        identificacion,
        factura.getTotalSinImpuestos().toPlainString(),
        factura.getTotalDescuento().toPlainString(),
        totalConImpuestosXml.toString(),
        factura.getPropina().toPlainString(),
        factura.getImporteTotal().toPlainString(),
        pagosXml.toString(),

        // detalles
        detallesXml.toString(),

        // info adicional
        infoAdicionalXml.toString());

    return xml.trim();
  }
}
