package com.factura.facturacion.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.catalogo.Producto;
import com.factura.facturacion.entidades.catalogo.ProductoImpuesto;

@Repository
public interface ProductoImpuestoRepositorio extends JpaRepository<ProductoImpuesto, Long> {

    // Todos los impuestos configurados para un producto
    List<ProductoImpuesto> findByProducto(Producto producto);
}
//Permite buscar todos los impuestos asociados a un producto específico
//Maneja CRUD automático