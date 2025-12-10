package com.factura.facturacion.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.catalogo.Producto;

@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, Long> {

    // Buscar por código principal
    Optional<Producto> findByCodigoPrincipal(String codigoPrincipal);

    // Buscar productos por descripción parcial (para buscador)
    List<Producto> findByDescripcionContainingIgnoreCase(String texto);

    // Buscar todos los productos activos
    List<Producto> findByActivoTrue();
}
// Permite buscar un producto por su código principal
// Permite buscar un producto por su código auxiliar
// Permite buscar productos cuya descripción contenga un texto específico
// Permite buscar todos los productos que están activos
// Maneja CRUD automático