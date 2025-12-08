package com.factura.facturacion.servicios.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.catalogo.Producto;
import com.factura.facturacion.repositorios.ProductoRepositorio;

@Service
public class ProductoServicio {

    private final ProductoRepositorio productoRepositorio;

    public ProductoServicio(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
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

    public Optional<Producto> buscarPorCodigoAuxiliar(String codigo) {
        return productoRepositorio.findByCodigoAuxiliar(codigo);
    }

    public List<Producto> buscarPorDescripcion(String texto) {
        return productoRepositorio.findByDescripcionContainingIgnoreCase(texto);
    }

    public List<Producto> listarActivos() {
        return productoRepositorio.findByActivoTrue();
    }

    public Producto guardar(Producto producto) {
        return productoRepositorio.save(producto);
    }

    public void eliminarPorId(Long id) {
        productoRepositorio.findById(id).ifPresent(producto -> {
            producto.setActivo(false);
            productoRepositorio.save(producto);
        });
    }
}
