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
        private final com.factura.facturacion.repositorios.ImpuestoTarifaRepositorio impuestoTarifaRepositorio;
        private final com.factura.facturacion.repositorios.ImpuestoRepositorio impuestoRepositorio;

        public FacturaEmisionServicio(EmpresaServicio empresaServicio,
                        EstablecimientoServicio establecimientoServicio,
                        PuntoEmisionServicio puntoEmisionServicio,
                        ClienteServicio clienteServicio,
                        ProductoServicio productoServicio,
                        ProductoImpuestoServicio productoImpuestoServicio,
                        FormaPagoServicio formaPagoServicio,
                        SecuencialDocumentoServicio secuencialDocumentoServicio,
                        FacturaServicio facturaServicio,
                        com.factura.facturacion.servicios.sri.SriEnvioServicio sriEnvioServicio,
                        com.factura.facturacion.repositorios.ImpuestoTarifaRepositorio impuestoTarifaRepositorio,
                        com.factura.facturacion.repositorios.ImpuestoRepositorio impuestoRepositorio) {
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
                this.impuestoTarifaRepositorio = impuestoTarifaRepositorio;
                this.impuestoRepositorio = impuestoRepositorio;
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
                BigDecimal subtotalIva15 = BigDecimal.ZERO; // NEW FIELD
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
                        // detalle.setCodigoAuxiliar(producto.getCodigoAuxiliar());
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

                        // Determinar los impuestos a aplicar
                        // Lógica: Si viene el IVA en el DTO, forzamos ese. Si no, usamos los del
                        // producto.
                        List<ImpuestoTarifa> tarifasAplicar = new java.util.ArrayList<>();

                        // Override logic
                        if (detDto.getIva() != null && !detDto.getIva().isEmpty()) {
                                String ivaSolicitado = detDto.getIva();
                                String codigoTarifaBusqueda = "2"; // Default 12%

                                // Mapping simple (debería ser más robusto, pero funciona para lo urgente)
                                // Reference: Table 17 SRI
                                if ("12".equals(ivaSolicitado))
                                        codigoTarifaBusqueda = "2";
                                else if ("15".equals(ivaSolicitado))
                                        codigoTarifaBusqueda = "4";
                                else if ("0".equals(ivaSolicitado))
                                        codigoTarifaBusqueda = "0";
                                else if ("NO".equals(ivaSolicitado))
                                        codigoTarifaBusqueda = "6"; // No objeto
                                else if ("EX".equals(ivaSolicitado))
                                        codigoTarifaBusqueda = "7"; // Exento
                                else {
                                        // Fallback try to find exact percentage if it's a number like "13"
                                        // Not implemented yet, assumes standard codes.
                                }

                                // Buscar la tarifa en BD (Impuesto 2 = IVA)
                                java.util.Optional<ImpuestoTarifa> tarifaOpt = impuestoTarifaRepositorio
                                                .findByImpuesto_CodigoAndCodigoTarifa("2", codigoTarifaBusqueda);

                                if (tarifaOpt.isPresent()) {
                                        tarifasAplicar.add(tarifaOpt.get());
                                } else {
                                        // AUTO-HEALING: Create missing tariff (especially for 15%)
                                        if ("4".equals(codigoTarifaBusqueda)) {
                                                com.factura.facturacion.entidades.catalogo.Impuesto impuestoIva = impuestoRepositorio
                                                                .findByCodigo("2").orElse(null);
                                                if (impuestoIva != null) {
                                                        ImpuestoTarifa nuevaTarifa = new ImpuestoTarifa();
                                                        nuevaTarifa.setImpuesto(impuestoIva);
                                                        nuevaTarifa.setCodigoTarifa("4");
                                                        nuevaTarifa.setPorcentaje(new BigDecimal("15.00"));
                                                        nuevaTarifa.setDescripcion("IVA 15%");
                                                        nuevaTarifa.setActivo(true);

                                                        nuevaTarifa = impuestoTarifaRepositorio.save(nuevaTarifa);
                                                        tarifasAplicar.add(nuevaTarifa);
                                                } else {
                                                        // Fallback if Impuesto IVA not found
                                                        productoImpuestoServicio.listarPorProducto(producto)
                                                                        .forEach(pi -> tarifasAplicar
                                                                                        .add(pi.getImpuestoTarifa()));
                                                }
                                        } else {
                                                // Fallback to product taxes
                                                productoImpuestoServicio.listarPorProducto(producto)
                                                                .forEach(pi -> tarifasAplicar
                                                                                .add(pi.getImpuestoTarifa()));
                                        }
                                }
                        } else {
                                // Default behavior
                                productoImpuestoServicio.listarPorProducto(producto)
                                                .forEach(pi -> tarifasAplicar.add(pi.getImpuestoTarifa()));
                        }

                        for (ImpuestoTarifa tarifa : tarifasAplicar) {

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

                                // Sumar a totales de la factura
                                if ("2".equals(codigoImpuesto)) { // IVA
                                        String codigoTarifa = tarifa.getCodigoTarifa();
                                        if ("2".equals(codigoTarifa)) { // IVA 12%
                                                subtotalIva12 = subtotalIva12.add(precioTotalSinImp);
                                                valorIva = valorIva.add(valorImp);
                                        } else if ("4".equals(codigoTarifa)) { // IVA 15% (NEW)
                                                subtotalIva15 = subtotalIva15.add(precioTotalSinImp);
                                                valorIva = valorIva.add(valorImp);
                                        } else if ("0".equals(codigoTarifa)) { // IVA 0%
                                                subtotalIva0 = subtotalIva0.add(precioTotalSinImp);
                                        } else if ("6".equals(codigoTarifa)) { // No objeto
                                                subtotalNoObjeto = subtotalNoObjeto.add(precioTotalSinImp);
                                        } else if ("7".equals(codigoTarifa)) { // Exento
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
                factura.setSubtotalIva15(subtotalIva15); // Set new field
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

                // 9. ENVIAR AUTOMÁTICAMENTE AL SRI (Firma + Envío)
                try {
                        System.out.println(">> Iniciando envío automático al SRI...");
                        facturaGuardada = sriEnvioServicio.enviar(facturaGuardada);
                        System.out.println(">> Envío completado. Estado: " + facturaGuardada.getEstado());
                } catch (Exception e) {
                        System.err.println(">> ERROR en envío SRI: " + e.getMessage());
                        e.printStackTrace();
                        // Guardar el error en la factura para que el frontend lo vea
                        facturaGuardada.setEstado("RECHAZADA");
                        facturaGuardada.setMensajeError("Error al enviar al SRI: " + e.getMessage());
                        facturaGuardada = facturaServicio.guardar(facturaGuardada);
                        // Re-lanzar para que el frontend reciba el error
                        throw new RuntimeException("Error al enviar factura al SRI: " + e.getMessage(), e);
                }

                return facturaGuardada;
        }
}
