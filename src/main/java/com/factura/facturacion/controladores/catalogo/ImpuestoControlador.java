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
import org.springframework.web.bind.annotation.RestController;

import com.factura.facturacion.entidades.catalogo.Impuesto;
import com.factura.facturacion.servicios.catalogo.ImpuestoServicio;

@RestController
@RequestMapping("/api/impuestos")
public class ImpuestoControlador {

    private final ImpuestoServicio impuestoServicio;

    public ImpuestoControlador(ImpuestoServicio impuestoServicio) {
        this.impuestoServicio = impuestoServicio;
    }

    // Listar todos los impuestos
    @GetMapping
    public List<Impuesto> listar() {
        return impuestoServicio.listarTodos();
    }

    // Buscar impuesto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Impuesto> buscarPorId(@PathVariable Long id) {
        return impuestoServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar impuesto por código (2 = IVA, 3 = ICE, 5 = IRBPNR)
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Impuesto> buscarPorCodigo(@PathVariable String codigo) {
        return impuestoServicio.buscarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nuevo impuesto
    @PostMapping
    public Impuesto crear(@RequestBody Impuesto impuesto) {
        return impuestoServicio.guardar(impuesto);
    }

    // Actualizar impuesto
    @PutMapping("/{id}")
    public ResponseEntity<Impuesto> actualizar(@PathVariable Long id,
                                               @RequestBody Impuesto impuesto) {
        return impuestoServicio.buscarPorId(id)
                .map(existente -> {
                    impuesto.setId(id);
                    Impuesto actualizado = impuestoServicio.guardar(impuesto);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar impuesto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (impuestoServicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        impuestoServicio.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
