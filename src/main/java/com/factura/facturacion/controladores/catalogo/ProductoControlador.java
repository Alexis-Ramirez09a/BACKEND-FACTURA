package com.factura.facturacion.controladores.catalogo;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.factura.facturacion.entidades.catalogo.Producto;
import com.factura.facturacion.servicios.catalogo.ProductoServicio;

@RestController
@RequestMapping("/api/productos")
public class ProductoControlador {

    private final ProductoServicio productoServicio;

    public ProductoControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    // Listar todos los productos
    @GetMapping
    public List<Producto> listar() {
        return productoServicio.listarActivos();
    }

    // Listar solo productos activos
    @GetMapping("/activos")
    public List<Producto> listarActivos() {
        return productoServicio.listarActivos();
    }

    // Buscar producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return productoServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar por código principal
    @GetMapping("/codigo-principal/{codigo}")
    public ResponseEntity<Producto> buscarPorCodigoPrincipal(@PathVariable String codigo) {
        return productoServicio.buscarPorCodigoPrincipal(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar productos por texto en la descripción
    @GetMapping("/buscar")
    public List<Producto> buscarPorDescripcion(@RequestParam("q") String texto) {
        return productoServicio.buscarPorDescripcion(texto);
    }

    // Crear nuevo producto
    @PostMapping
    public Producto crear(@jakarta.validation.Valid @RequestBody Producto producto) {
        return productoServicio.guardar(producto);
    }

    // Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id,
            @RequestBody Producto producto) {
        return productoServicio.buscarPorId(id)
                .map(existente -> {
                    producto.setId(id);
                    Producto actualizado = productoServicio.guardar(producto);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    // @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (productoServicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        productoServicio.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
