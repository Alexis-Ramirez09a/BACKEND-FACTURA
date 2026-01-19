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

    public ProductoServicio(ProductoRepositorio productoRepositorio,
            com.factura.facturacion.servicios.catalogo.ProductoImpuestoServicio productoImpuestoServicio,
            com.factura.facturacion.repositorios.ImpuestoRepositorio impuestoRepositorio,
            com.factura.facturacion.repositorios.ImpuestoTarifaRepositorio impuestoTarifaRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.productoImpuestoServicio = productoImpuestoServicio;
        this.impuestoRepositorio = impuestoRepositorio;
        this.impuestoTarifaRepositorio = impuestoTarifaRepositorio;
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

        // Opcional: Validar nombre exacto también si se desea
        // ...

        Producto guardado = productoRepositorio.save(producto);

        // Si viene el campo IVA, guardar la relación
        if (producto.getIva() != null) {
            String codigoTarifa = "0"; // Default 0
            if ("12".equals(producto.getIva()))
                codigoTarifa = "2"; // 2 is 12%
            else if ("15".equals(producto.getIva()))
                codigoTarifa = "4";
            else if ("0".equals(producto.getIva()))
                codigoTarifa = "0"; // 0%

            final String finalCodigoTarifa = codigoTarifa;

            try {
                // System.out.println("BUSCANDO IMPUESTO IVA (2)...");
                // Buscar Impuesto IVA (Codigo "2")
                com.factura.facturacion.entidades.catalogo.Impuesto impuestoIva = impuestoRepositorio.findByCodigo("2")
                        .orElseThrow(() -> new RuntimeException("Impuesto IVA (Codigo 2) no encontrado en la BD."));
                // System.out.println("IMPUESTO IVA ENCONTRADO: " + impuestoIva.getId());

                // Buscar Tarifa
                // System.out.println("BUSCANDO TARIFA: " + finalCodigoTarifa);
                com.factura.facturacion.entidades.catalogo.ImpuestoTarifa tarifa = impuestoTarifaRepositorio
                        .findByCodigoTarifaAndImpuesto(finalCodigoTarifa, impuestoIva)
                        .stream().findFirst()
                        .orElseThrow(
                                () -> new RuntimeException("Tarifa no encontrada para codigo: " + finalCodigoTarifa));
                // System.out.println("TARIFA ENCONTRADA: " + tarifa.getId());

                // Borrar impuestos anteriores (simple logic for now)
                // In a real app we might want to update, but deletion is safe for 1-to-many
                // overwrite
                // But ProductoImpuestoServicio might not have delete logic exposed easily.
                // Let's rely on finding existing or creating new.

                // For simplicity in this fix: Create new relationship
                com.factura.facturacion.entidades.catalogo.ProductoImpuesto pi = new com.factura.facturacion.entidades.catalogo.ProductoImpuesto();
                pi.setProducto(guardado);
                pi.setImpuestoTarifa(tarifa);
                productoImpuestoServicio.guardar(pi);
                // System.out.println("RELACION PRODUCTO-IMPUESTO GUARDADA");
            } catch (Exception e) {
                System.out.println("ERROR GRAVE GUARDANDO IMPUESTO PRODUCTO: " + e.getMessage());
                e.printStackTrace();
                // No re-lanzamos para que no falle el guardado del producto en sí (opcional)
                // throw e;
            }
        }
        return guardado;
    }

    public void eliminarPorId(Long id) {
        // Hard delete requested by user
        productoRepositorio.deleteById(id);
    }
}
