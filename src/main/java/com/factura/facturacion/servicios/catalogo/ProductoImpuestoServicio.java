package com.factura.facturacion.servicios.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.catalogo.Producto;
import com.factura.facturacion.entidades.catalogo.ProductoImpuesto;
import com.factura.facturacion.repositorios.ProductoImpuestoRepositorio;

@Service
public class ProductoImpuestoServicio {

    private final ProductoImpuestoRepositorio productoImpuestoRepositorio;

    public ProductoImpuestoServicio(ProductoImpuestoRepositorio productoImpuestoRepositorio) {
        this.productoImpuestoRepositorio = productoImpuestoRepositorio;
    }

    public List<ProductoImpuesto> listarTodos() {
        return productoImpuestoRepositorio.findAll();
    }

    public Optional<ProductoImpuesto> buscarPorId(Long id) {
        return productoImpuestoRepositorio.findById(id);
    }

    public List<ProductoImpuesto> listarPorProducto(Producto producto) {
        return productoImpuestoRepositorio.findByProducto(producto);
    }

    public ProductoImpuesto guardar(ProductoImpuesto productoImpuesto) {
        return productoImpuestoRepositorio.save(productoImpuesto);
    }

    public void eliminarPorId(Long id) {
        productoImpuestoRepositorio.deleteById(id);
    }
}
//organiza y expone todas las operaciones que se pueden hacer con ProductoImpuesto, usando el repositorio, y luego esta clase será llamada desde los controladores REST.