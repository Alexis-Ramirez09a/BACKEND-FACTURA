package com.factura.facturacion.servicios.factura;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.factura.facturacion.dtos.factura.FacturaCrearDto;
import com.factura.facturacion.dtos.factura.FacturaDetalleCrearDto;
import com.factura.facturacion.dtos.factura.FacturaPagoCrearDto;
import com.factura.facturacion.entidades.catalogo.Cliente;
import com.factura.facturacion.entidades.catalogo.FormaPago;
import com.factura.facturacion.entidades.catalogo.ImpuestoTarifa;
import com.factura.facturacion.entidades.catalogo.Producto;
import com.factura.facturacion.entidades.catalogo.ProductoImpuesto;
import com.factura.facturacion.entidades.empresa.Empresa;
import com.factura.facturacion.entidades.empresa.Establecimiento;
import com.factura.facturacion.entidades.empresa.PuntoEmision;
import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.entidades.factura.FacturaDetalle;
import com.factura.facturacion.entidades.factura.FacturaDetalleImpuesto;
import com.factura.facturacion.entidades.factura.FacturaPago;
import com.factura.facturacion.servicios.catalogo.ClienteServicio;
import com.factura.facturacion.servicios.catalogo.FormaPagoServicio;
import com.factura.facturacion.servicios.catalogo.ProductoImpuestoServicio;
import com.factura.facturacion.servicios.catalogo.ProductoServicio;
import com.factura.facturacion.servicios.empresa.EmpresaServicio;
import com.factura.facturacion.servicios.empresa.EstablecimientoServicio;
import com.factura.facturacion.servicios.empresa.PuntoEmisionServicio;
import com.factura.facturacion.util.sri.ClaveAccesoUtil;

@Service
public class FacturaEmisionServicio {

        private final EmpresaServicio empresaServicio;
        private final EstablecimientoServicio establecimientoServicio;
        private final PuntoEmisionServicio puntoEmisionServicio;
        private final ClienteServicio clienteServicio;
        private final ProductoServicio productoServicio;
        private final ProductoImpuestoServicio productoImpuestoServicio;
        private final FormaPagoServicio formaPagoServicio;
        private final SecuencialDocumentoServicio secuencialDocumentoServicio;
        private final FacturaServicio facturaServicio;
        private final com.factura.facturacion.servicios.sri.SriEnvioServicio sriEnvioServicio;

        public FacturaEmisionServicio(EmpresaServicio empresaServicio,
                        EstablecimientoServicio establecimientoServicio,
                        PuntoEmisionServicio puntoEmisionServicio,
                        ClienteServicio clienteServicio,
                        ProductoServicio productoServicio,
                        ProductoImpuestoServicio productoImpuestoServicio,
                        FormaPagoServicio formaPagoServicio,
                        SecuencialDocumentoServicio secuencialDocumentoServicio,
                        FacturaServicio facturaServicio,
                        com.factura.facturacion.servicios.sri.SriEnvioServicio sriEnvioServicio) {
                this.empresaServicio = empresaServicio;
                this.establecimientoServicio = establecimientoServicio;
                this.puntoEmisionServicio = puntoEmisionServicio;
                this.clienteServicio = clienteServicio;
                this.productoServicio = productoServicio;
                this.productoImpuestoServicio = productoImpuestoServicio;
                this.formaPagoServicio = formaPagoServicio;
                this.secuencialDocumentoServicio = secuencialDocumentoServicio;
                this.facturaServicio = facturaServicio;
                this.sriEnvioServicio = sriEnvioServicio;
        }

        @Transactional
        public Factura emitirFactura(FacturaCrearDto dto) {
                // 1. Cargar entidades base
                Empresa empresa;
                if (dto.getEmpresaId() != null) {
                        empresa = empresaServicio.buscarPorId(dto.getEmpresaId())
                                        .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
                } else {
                        // Default: Primera empresa encontrada
                        empresa = empresaServicio.listarTodas().stream().findFirst()
                                        .orElseThrow(() -> new RuntimeException(
                                                        "No existe ninguna empresa configurada"));
                }

                Establecimiento establecimiento;
                if (dto.getEstablecimientoId() != null) {
                        establecimiento = establecimientoServicio.buscarPorId(dto.getEstablecimientoId())
                                        .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));
                } else {
                        // Default: Primer establecimiento de la empresa
                        establecimiento = establecimientoServicio.listarPorEmpresa(empresa).stream().findFirst()
                                        .orElseThrow(() -> new RuntimeException(
                                                        "No hay establecimientos para la empresa"));
                }

                PuntoEmision puntoEmision;
                if (dto.getPuntoEmisionId() != null) {
                        puntoEmision = puntoEmisionServicio.buscarPorId(dto.getPuntoEmisionId())
                                        .orElseThrow(() -> new RuntimeException("Punto de emisión no encontrado"));
                } else {
                        // Default: Primer punto de emision del establecimiento
                        puntoEmision = puntoEmisionServicio.listarPorEstablecimiento(establecimiento).stream()
                                        .findFirst()
                                        .orElseThrow(() -> new RuntimeException(
                                                        "No hay puntos de emisión para el establecimiento"));
                }

                Cliente cliente = clienteServicio.buscarPorId(dto.getClienteId())
                                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

                LocalDate fechaEmision = dto.getFechaEmision() != null ? dto.getFechaEmision() : LocalDate.now();

                // 2. Generar secuencial
                String tipoComprobante = "01"; // factura
                String secuencial = secuencialDocumentoServicio.generarSiguienteSecuencial(
                                tipoComprobante, establecimiento, puntoEmision);

                // 3. Crear objeto factura
                Factura factura = new Factura();
                factura.setEmpresa(empresa);
                factura.setEstablecimiento(establecimiento);
                factura.setPuntoEmision(puntoEmision);
                factura.setCliente(cliente);
                factura.setTipoComprobante(tipoComprobante);
                factura.setCodigoEstablecimiento(establecimiento.getCodigo());
                factura.setCodigoPuntoEmision(puntoEmision.getCodigo());
                factura.setSecuencial(secuencial);
                factura.setFechaEmision(fechaEmision);

                // Datos del comprador
                factura.setTipoIdentificacionComprador(cliente.getTipoIdentificacion());
                factura.setIdentificacionComprador(cliente.getIdentificacion());
                factura.setRazonSocialComprador(cliente.getNombreRazonSocial());
                factura.setDireccionComprador(cliente.getDireccion());
                factura.setObservacion(dto.getObservacion());
                factura.setGuiaRemision(dto.getGuiaRemision());

                // 4. Procesar detalles
                BigDecimal totalSinImpuestos = BigDecimal.ZERO;
                BigDecimal totalDescuento = BigDecimal.ZERO;
                BigDecimal subtotalIva12 = BigDecimal.ZERO;
                BigDecimal subtotalIva0 = BigDecimal.ZERO;
                BigDecimal subtotalNoObjeto = BigDecimal.ZERO;
                BigDecimal subtotalExento = BigDecimal.ZERO;
                BigDecimal valorIva = BigDecimal.ZERO;

                for (FacturaDetalleCrearDto detDto : dto.getDetalles()) {
                        Producto producto = productoServicio.buscarPorId(detDto.getProductoId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Producto no encontrado: " + detDto.getProductoId()));

                        FacturaDetalle detalle = new FacturaDetalle();
                        detalle.setFactura(factura);
                        detalle.setProducto(producto);
                        detalle.setCodigoPrincipal(producto.getCodigoPrincipal());
                        // detalle.setCodigoAuxiliar(producto.getCodigoAuxiliar()); // Removed from
                        // product
                        detalle.setDescripcion(detDto.getDescripcion() != null ? detDto.getDescripcion()
                                        : producto.getDescripcion());

                        BigDecimal cantidad = detDto.getCantidad();
                        BigDecimal precioUnitario = detDto.getPrecioUnitario() != null
                                        ? detDto.getPrecioUnitario()
                                        : producto.getPrecioUnitario();
                        BigDecimal descuento = detDto.getDescuento() != null ? detDto.getDescuento() : BigDecimal.ZERO;

                        detalle.setCantidad(cantidad);
                        detalle.setPrecioUnitario(precioUnitario);
                        detalle.setDescuento(descuento);

                        // --- CONTROL Y REDUCCIÓN DE STOCK ---
                        int cantidadVenta = cantidad.intValue();
                        if (producto.getCantidad() < cantidadVenta) {
                                throw new RuntimeException("Stock insuficiente para: " + producto.getDescripcion()
                                                + ". Disponible: " + producto.getCantidad());
                        }
                        producto.setCantidad(producto.getCantidad() - cantidadVenta);
                        productoServicio.guardar(producto);
                        // ------------------------------------

                        BigDecimal precioTotalSinImp = cantidad.multiply(precioUnitario)
                                        .subtract(descuento != null ? descuento : BigDecimal.ZERO)
                                        .setScale(2, RoundingMode.HALF_UP);

                        detalle.setPrecioTotalSinImpuesto(precioTotalSinImp);

                        totalSinImpuestos = totalSinImpuestos.add(precioTotalSinImp);
                        totalDescuento = totalDescuento.add(descuento != null ? descuento : BigDecimal.ZERO);

                        // Impuestos del producto
                        List<ProductoImpuesto> productosImpuestos = productoImpuestoServicio
                                        .listarPorProducto(producto);
                        for (ProductoImpuesto pi : productosImpuestos) {
                                ImpuestoTarifa tarifa = pi.getImpuestoTarifa();

                                FacturaDetalleImpuesto detImp = new FacturaDetalleImpuesto();
                                detImp.setDetalle(detalle);
                                detImp.setImpuestoTarifa(tarifa);
                                detImp.setBaseImponible(precioTotalSinImp);

                                BigDecimal porcentaje = tarifa.getPorcentaje() != null ? tarifa.getPorcentaje()
                                                : BigDecimal.ZERO;
                                detImp.setPorcentaje(porcentaje);

                                String codigoImpuesto = tarifa.getImpuesto().getCodigo(); // 2 = IVA
                                detImp.setCodigoImpuesto(codigoImpuesto);
                                detImp.setCodigoPorcentaje(tarifa.getCodigoTarifa());

                                BigDecimal valorImp = precioTotalSinImp
                                                .multiply(porcentaje)
                                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                                detImp.setValor(valorImp);

                                // Sumar a totales de la factura según tipo de impuesto/tarifa
                                if ("2".equals(codigoImpuesto)) { // IVA
                                        if ("2".equals(tarifa.getCodigoTarifa())) { // IVA 12%
                                                subtotalIva12 = subtotalIva12.add(precioTotalSinImp);
                                                valorIva = valorIva.add(valorImp);
                                        } else if ("0".equals(tarifa.getCodigoTarifa())) { // IVA 0%
                                                subtotalIva0 = subtotalIva0.add(precioTotalSinImp);
                                        } else if ("6".equals(tarifa.getCodigoTarifa())) { // No objeto
                                                subtotalNoObjeto = subtotalNoObjeto.add(precioTotalSinImp);
                                        } else if ("7".equals(tarifa.getCodigoTarifa())) { // Exento
                                                subtotalExento = subtotalExento.add(precioTotalSinImp);
                                        }
                                }

                                detalle.getImpuestos().add(detImp);
                        }

                        factura.getDetalles().add(detalle);
                }

                // 6. Totales de factura
                factura.setTotalSinImpuestos(totalSinImpuestos);
                factura.setTotalDescuento(totalDescuento);
                factura.setSubtotalIva12(subtotalIva12);
                factura.setSubtotalIva0(subtotalIva0);
                factura.setSubtotalNoObjetoIva(subtotalNoObjeto);
                factura.setSubtotalExentoIva(subtotalExento);
                factura.setValorIva(valorIva);
                factura.setValorIce(BigDecimal.ZERO);
                factura.setValorIrbpnr(BigDecimal.ZERO);

                BigDecimal importeTotal = totalSinImpuestos
                                .add(factura.getValorIva())
                                .add(factura.getValorIce())
                                .add(factura.getValorIrbpnr())
                                .setScale(2, RoundingMode.HALF_UP);

                factura.setImporteTotal(importeTotal);

                // AHORA Procesar pagos (Moved Logic)
                if (dto.getPagos() != null && !dto.getPagos().isEmpty()) {
                        for (FacturaPagoCrearDto pagoDto : dto.getPagos()) {
                                // ... same logic loop ...
                                FormaPago formaPago = formaPagoServicio
                                                .buscarPorCodigoSri(pagoDto.getCodigoFormaPagoSri())
                                                .orElseThrow(() -> new RuntimeException("Forma de pago no encontrada: "
                                                                + pagoDto.getCodigoFormaPagoSri()));

                                FacturaPago pago = new FacturaPago();
                                pago.setFactura(factura);
                                pago.setFormaPago(formaPago);
                                pago.setTotal(pagoDto.getTotal());
                                pago.setPlazo(pagoDto.getPlazo() != null ? pagoDto.getPlazo() : 0);
                                pago.setUnidadTiempo(pagoDto.getUnidadTiempo());

                                factura.getPagos().add(pago);
                        }
                } else {
                        // Default Payment Logic
                        FacturaPago pago = new FacturaPago();
                        pago.setFactura(factura);

                        String codigoFormaPago = dto.getFormaPago() != null ? dto.getFormaPago() : "01";
                        FormaPago formaPago = formaPagoServicio.buscarPorCodigoSri(codigoFormaPago)
                                        .orElse(formaPagoServicio.buscarPorCodigoSri("01").orElse(null)); // Fallback a
                                                                                                          // efectivo

                        if (formaPago == null)
                                throw new RuntimeException("No se encontro forma de pago");

                        pago.setFormaPago(formaPago);
                        pago.setTotal(importeTotal); // Total exacto
                        pago.setPlazo(dto.getTiempo());
                        pago.setUnidadTiempo(dto.getPlazo()); // DIAS/MESES
                        factura.getPagos().add(pago);
                }

                // 7. Generar clave de acceso
                String serie = factura.getCodigoEstablecimiento() + factura.getCodigoPuntoEmision();

                String codigoNumerico = String.format("%08d", new Random().nextInt(100000000));

                String claveAcceso = ClaveAccesoUtil.generarClaveAcceso(
                                fechaEmision,
                                tipoComprobante,
                                empresa.getRuc(),
                                empresa.getAmbiente(), // "1" pruebas, "2" producción
                                serie,
                                secuencial,
                                codigoNumerico,
                                empresa.getTipoEmision() // normalmente "1"
                );

                factura.setClaveAcceso(claveAcceso);

                // Estado inicial
                factura.setEstado("PENDIENTE");

                // 8. Guardar factura completa
                Factura facturaGuardada = facturaServicio.guardar(factura);

                try {
                        // Solo generamos el XML (el método enviar ya no cambia estado a 'RECIBIDA')
                        sriEnvioServicio.enviar(facturaGuardada);
                        // sriEnvioServicio.autorizar(facturaGuardada); // ELIMINADO para evitar
                        // auto-aprobación
                } catch (Exception e) {
                        System.out.println("Error generando XML: " + e.getMessage());
                }

                return facturaGuardada;
        }
}
