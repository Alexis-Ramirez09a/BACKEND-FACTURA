package com.factura.facturacion.controladores.empresa;

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

import com.factura.facturacion.entidades.empresa.Establecimiento;
import com.factura.facturacion.entidades.empresa.PuntoEmision;
import com.factura.facturacion.servicios.empresa.EstablecimientoServicio;
import com.factura.facturacion.servicios.empresa.PuntoEmisionServicio;

@RestController
@RequestMapping("/api/puntos-emision")
public class PuntoEmisionControlador {

    private final PuntoEmisionServicio puntoEmisionServicio;
    private final EstablecimientoServicio establecimientoServicio;

    public PuntoEmisionControlador(PuntoEmisionServicio puntoEmisionServicio,
                                   EstablecimientoServicio establecimientoServicio) {
        this.puntoEmisionServicio = puntoEmisionServicio;
        this.establecimientoServicio = establecimientoServicio;
    }

    // Listar todos los puntos de emisión
    @GetMapping
    public List<PuntoEmision> listar() {
        return puntoEmisionServicio.listarTodos();
    }

    // Buscar un punto de emisión por ID
    @GetMapping("/{id}")
    public ResponseEntity<PuntoEmision> buscarPorId(@PathVariable Long id) {
        return puntoEmisionServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar puntos de emisión por establecimiento
    @GetMapping("/establecimiento/{establecimientoId}")
    public ResponseEntity<List<PuntoEmision>> listarPorEstablecimiento(@PathVariable Long establecimientoId) {
        return establecimientoServicio.buscarPorId(establecimientoId)
                .map(est -> ResponseEntity.ok(
                        puntoEmisionServicio.listarPorEstablecimiento(est)
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear punto de emisión (recibe JSON con establecimiento.id)
    @PostMapping
    public ResponseEntity<PuntoEmision> crear(@RequestBody PuntoEmision puntoEmision) {
        Establecimiento est = puntoEmision.getEstablecimiento();
        if (est == null || est.getId() == null ||
                establecimientoServicio.buscarPorId(est.getId()).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        PuntoEmision guardado = puntoEmisionServicio.guardar(puntoEmision);
        return ResponseEntity.ok(guardado);
    }

    // Actualizar punto de emisión
    @PutMapping("/{id}")
    public ResponseEntity<PuntoEmision> actualizar(@PathVariable Long id,
                                                   @RequestBody PuntoEmision puntoEmision) {
        return puntoEmisionServicio.buscarPorId(id)
                .map(existente -> {
                    puntoEmision.setId(id);
                    PuntoEmision actualizado = puntoEmisionServicio.guardar(puntoEmision);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar punto de emisión
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (puntoEmisionServicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        puntoEmisionServicio.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
