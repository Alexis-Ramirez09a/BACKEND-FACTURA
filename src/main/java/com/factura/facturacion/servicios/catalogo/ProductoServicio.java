package com.factura.facturacion.servicios.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.catalogo.Producto;
import com.factura.facturacion.repositorios.ProductoRepositorio;

@Service
public class ProductoServicio {

    private final ProductoRepositorio productoRepositorio;
    private final com.factura.facturacion.servicios.catalogo.ProductoImpuestoServicio productoImpuestoServicio;
    private final com.factura.facturacion.repositorios.ImpuestoRepositorio impuestoRepositorio;
    private final com.factura.facturacion.repositorios.ImpuestoTarifaRepositorio impuestoTarifaRepositorio;
    private final com.factura.facturacion.repositorios.ProductoImpuestoRepositorio productoImpuestoRepositorio;

    public ProductoServicio(ProductoRepositorio productoRepositorio,
            com.factura.facturacion.servicios.catalogo.ProductoImpuestoServicio productoImpuestoServicio,
            com.factura.facturacion.repositorios.ImpuestoRepositorio impuestoRepositorio,
            com.factura.facturacion.repositorios.ImpuestoTarifaRepositorio impuestoTarifaRepositorio,
            com.factura.facturacion.repositorios.ProductoImpuestoRepositorio productoImpuestoRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.productoImpuestoServicio = productoImpuestoServicio;
        this.impuestoRepositorio = impuestoRepositorio;
        this.impuestoTarifaRepositorio = impuestoTarifaRepositorio;
        this.productoImpuestoRepositorio = productoImpuestoRepositorio;
    }

    public List<Producto> listarTodos() {
        return productoRepositorio.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepositorio.findById(id);
    }

    public Optional<Producto> buscarPorCodigoPrincipal(String codigo) {
        return productoRepositorio.findByCodigoPrincipal(codigo);
    }

    public List<Producto> buscarPorDescripcion(String texto) {
        return productoRepositorio.findByDescripcionContainingIgnoreCaseOrCodigoPrincipalContainingIgnoreCase(texto,
                texto);
    }

    public List<Producto> listarActivos() {
        return productoRepositorio.findByActivoTrue();
    }

    @org.springframework.transaction.annotation.Transactional
    public Producto guardar(Producto producto) {
        // Validación de duplicados (Código Principal o Descripción exacta)
        Optional<Producto> existenteCodigo = productoRepositorio.findByCodigoPrincipal(producto.getCodigoPrincipal());
        if (existenteCodigo.isPresent()) {
            if (producto.getId() == null || !existenteCodigo.get().getId().equals(producto.getId())) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.CONFLICT,
                        "El código " + producto.getCodigoPrincipal() + " ya está registrado.");
            }
        }

        // Logic for update vs create to ensure we manipulate the managed entity
        Producto entityToSave;
        if (producto.getId() != null) {
            entityToSave = productoRepositorio.findById(producto.getId())
                    .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.NOT_FOUND, "Producto no encontrado"));

            // Update fields manually or via mapper
            entityToSave.setCodigoPrincipal(producto.getCodigoPrincipal());
            entityToSave.setDescripcion(producto.getDescripcion());
            entityToSave.setPrecioUnitario(producto.getPrecioUnitario());
            entityToSave.setCantidad(producto.getCantidad());
            entityToSave.setIva(producto.getIva()); // Transient field for transport
            entityToSave.setActivo(producto.getActivo());
        } else {
            entityToSave = producto;
        }

        // Si viene el campo IVA, actualizar la relación
        if (producto.getIva() != null && !producto.getIva().trim().isEmpty()) {
            System.out.println("[DEBUG] Procesando IVA para producto: " + producto.getIva());
            try {
                // Normalizar escala a 2 decimales para coincidir con BD (15 -> 15.00)
                java.math.BigDecimal porcentaje = new java.math.BigDecimal(producto.getIva())
                        .setScale(2, java.math.RoundingMode.HALF_UP);

                System.out.println("[DEBUG] Porcentaje normalizado: " + porcentaje);

                // Buscar Impuesto IVA (Codigo "2")
                com.factura.facturacion.entidades.catalogo.Impuesto impuestoIva = impuestoRepositorio.findByCodigo("2")
                        .orElseThrow(() -> new RuntimeException("Impuesto IVA (Codigo 2) no encontrado en la BD."));

                // Buscar Tarifa por Porcentaje (dinámico)
                java.util.Optional<com.factura.facturacion.entidades.catalogo.ImpuestoTarifa> tarifaOpt = impuestoTarifaRepositorio
                        .findByImpuestoAndPorcentaje(impuestoIva, porcentaje);

                // Fallback: Si no encuentra por exactitud decimal, intentar por códigos
                // estándar conocidos
                if (tarifaOpt.isEmpty()) {
                    System.out.println(
                            "[DEBUG] No encontrado por porcentaje exacto (" + porcentaje + "). Intentando fallback...");
                    String codigoFallback = null;
                    // Comparison with compareTo ignores scale, which is safer
                    if (porcentaje.compareTo(new java.math.BigDecimal("15")) == 0)
                        codigoFallback = "4";
                    else if (porcentaje.compareTo(new java.math.BigDecimal("12")) == 0)
                        codigoFallback = "2";
                    else if (porcentaje.compareTo(new java.math.BigDecimal("14")) == 0)
                        codigoFallback = "3";
                    else if (porcentaje.compareTo(java.math.BigDecimal.ZERO) == 0)
                        codigoFallback = "0";
                    else if (porcentaje.compareTo(new java.math.BigDecimal("5")) == 0)
                        codigoFallback = "5"; // Example 5%

                    if (codigoFallback != null) {
                        System.out.println("[DEBUG] Buscando por Codigo Fallback: " + codigoFallback);
                        tarifaOpt = impuestoTarifaRepositorio.findByCodigoTarifaAndImpuesto(codigoFallback,
                                impuestoIva);

                        // Self-healing: Crea la tax si no existe
                        if (tarifaOpt.isEmpty()) {
                            System.out.println("[DEBUG] Tarifa no existe en BD. AUTO-GENERANDO tarifa para código: "
                                    + codigoFallback);
                            com.factura.facturacion.entidades.catalogo.ImpuestoTarifa nuevaTarifa = new com.factura.facturacion.entidades.catalogo.ImpuestoTarifa();
                            nuevaTarifa.setImpuesto(impuestoIva);
                            nuevaTarifa.setCodigoTarifa(codigoFallback);
                            nuevaTarifa.setPorcentaje(porcentaje);
                            nuevaTarifa.setDescripcion("IVA " + porcentaje + "% (Auto-generado)");
                            nuevaTarifa.setActivo(true);

                            tarifaOpt = java.util.Optional.of(impuestoTarifaRepositorio.save(nuevaTarifa));
                        }
                    }
                }
                com.factura.facturacion.entidades.catalogo.ImpuestoTarifa tarifa = tarifaOpt
                        .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.BAD_REQUEST,
                                "No existe una tarifa de IVA configurada para el " + porcentaje
                                        + "%. (Intente verificar los decimales o la configuración de impuestos)"));

                // Manage relationships via Cascade (OrphanRemoval = true)
                // 1. Clear existing
                entityToSave.getImpuestos().clear();

                // 2. Add new
                com.factura.facturacion.entidades.catalogo.ProductoImpuesto pi = new com.factura.facturacion.entidades.catalogo.ProductoImpuesto();
                pi.setProducto(entityToSave);
                pi.setImpuestoTarifa(tarifa);

                entityToSave.getImpuestos().add(pi);

                // ACTUALIZACIÓN DE NUEVA COLUMNA (User Requirement)
                // Guardar la referencia directa a la tarifa también en la tabla productos
                entityToSave.setTarifaIva(tarifa);

            } catch (NumberFormatException e) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST, "El valor del IVA debe ser numérico.");
            } catch (Exception e) {
                System.out.println("ERROR GRAVE GUARDANDO IMPUESTO PRODUCTO: " + e.getMessage());
                e.printStackTrace();
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error al guardar impuesto: " + e.getMessage());
            }
        }

        // Save the parent, which cascades the children
        return productoRepositorio.save(entityToSave);
    }

    public void eliminarPorId(Long id) {
        // Hard delete requested by user
        productoRepositorio.deleteById(id);
    }
}
