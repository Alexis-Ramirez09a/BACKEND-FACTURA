package com.factura.facturacion.servicios.sri;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.factura.facturacion.entidades.catalogo.Cliente;
import com.factura.facturacion.entidades.catalogo.FormaPago;
import com.factura.facturacion.entidades.catalogo.Impuesto;
import com.factura.facturacion.entidades.catalogo.ImpuestoTarifa;
import com.factura.facturacion.entidades.catalogo.Producto;
import com.factura.facturacion.entidades.empresa.Empresa;
import com.factura.facturacion.entidades.empresa.Establecimiento;
import com.factura.facturacion.entidades.empresa.PuntoEmision;
import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.entidades.factura.FacturaDetalle;
import com.factura.facturacion.entidades.factura.FacturaDetalleImpuesto;
import com.factura.facturacion.entidades.factura.FacturaPago;

class SriXmlBuilderTest {

    @Test
    void testConstruirXmlFactura() {
        // 1. Prepare Data
        Empresa empresa = new Empresa();
        empresa.setRuc("1790011223001");
        empresa.setRazonSocial("MI EMPRESA S.A.");
        empresa.setNombreComercial("MI EMPRESA");
        empresa.setDireccionMatriz("Av. Amazonas y Naciones Unidas");
        empresa.setAmbiente("1");
        empresa.setTipoEmision("1");
        empresa.setAgenteRetencion("1");
        empresa.setContribuyenteRimpe("CONTRIBUYENTE RÉGIMEN RIMPE");

        Establecimiento est = new Establecimiento();
        est.setCodigo("001");
        est.setDireccion("Av. Amazonas");
        est.setEmpresa(empresa);

        PuntoEmision pto = new PuntoEmision();
        pto.setCodigo("001");
        pto.setEstablecimiento(est);

        Cliente cliente = new Cliente();
        cliente.setIdentificacion("1712345678");
        cliente.setTipoIdentificacion("05");
        cliente.setNombreRazonSocial("JUAN PEREZ");
        cliente.setCorreo("juan@mail.com");

        Factura factura = new Factura();
        factura.setEmpresa(empresa);
        factura.setEstablecimiento(est);
        factura.setPuntoEmision(pto);
        factura.setCliente(cliente);
        factura.setSecuencial("000000123");
        factura.setFechaEmision(LocalDate.now());
        factura.setClaveAcceso("1234567890123456789012345678901234567890123456789");
        factura.setGuiaRemision("001-001-000000999");
        factura.setObservacion("Venta de prueba");

        // Detalle
        FacturaDetalle det = new FacturaDetalle();
        Producto prod = new Producto();
        prod.setCodigoPrincipal("PROD01");
        prod.setDescripcion("Producto Prueba");
        det.setProducto(prod);
        det.setCantidad(new BigDecimal("2"));
        det.setPrecioUnitario(new BigDecimal("10.00"));
        det.setPrecioTotalSinImpuesto(new BigDecimal("20.00"));

        // Impuesto detalle
        FacturaDetalleImpuesto imp = new FacturaDetalleImpuesto();
        imp.setCodigoImpuesto("2");
        imp.setCodigoPorcentaje("2");
        imp.setBaseImponible(new BigDecimal("20.00"));
        imp.setValor(new BigDecimal("2.40"));
        ImpuestoTarifa tarifa = new ImpuestoTarifa();
        tarifa.setPorcentaje(new BigDecimal("12"));
        imp.setImpuestoTarifa(tarifa);
        det.setImpuestos(List.of(imp));

        factura.setDetalles(List.of(det));

        // Totales
        factura.setTotalSinImpuestos(new BigDecimal("20.00"));
        factura.setSubtotalIva12(new BigDecimal("20.00"));
        factura.setValorIva(new BigDecimal("2.40"));
        factura.setImporteTotal(new BigDecimal("22.40"));

        // Pagos
        FacturaPago pago = new FacturaPago();
        FormaPago fp = new FormaPago();
        fp.setCodigoSri("01");
        pago.setFormaPago(fp);
        pago.setTotal(new BigDecimal("22.40"));
        pago.setPlazo(30);
        pago.setUnidadTiempo("dias");
        factura.setPagos(List.of(pago));

        // 2. Execute
        SriXmlBuilderServicio builder = new SriXmlBuilderServicio();
        String xml = builder.construirXmlFactura(factura);

        System.out.println(xml);

        // 3. Verify
        assertTrue(xml.contains("<agenteRetencion>1</agenteRetencion>"));
        assertTrue(xml.contains("<contribuyenteRimpe>CONTRIBUYENTE RÉGIMEN RIMPE</contribuyenteRimpe>"));
        assertTrue(xml.contains("<guiaRemision>001-001-000000999</guiaRemision>"));
        assertTrue(xml.contains("<pagos>"));
        assertTrue(xml.contains("<formaPago>01</formaPago>"));
        assertTrue(xml.contains("<plazo>30</plazo>"));
        assertTrue(xml.contains("<unidadTiempo>dias</unidadTiempo>"));
        assertTrue(xml.contains("<campoAdicional nombre=\"Email\">juan@mail.com</campoAdicional>"));
    }
}
