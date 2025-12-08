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

import com.factura.facturacion.entidades.empresa.Empresa;
import com.factura.facturacion.entidades.empresa.Establecimiento;
import com.factura.facturacion.servicios.empresa.EmpresaServicio;
import com.factura.facturacion.servicios.empresa.EstablecimientoServicio;

@RestController
@RequestMapping("/api/establecimientos")
public class EstablecimientoControlador {

    private final EstablecimientoServicio establecimientoServicio;
    private final EmpresaServicio empresaServicio;

    public EstablecimientoControlador(EstablecimientoServicio establecimientoServicio,
                                      EmpresaServicio empresaServicio) {
        this.establecimientoServicio = establecimientoServicio;
        this.empresaServicio = empresaServicio;
    }

    // Listar todos los establecimientos
    @GetMapping
    public List<Establecimiento> listar() {
        return establecimientoServicio.listarTodos();
    }

    // Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<Establecimiento> buscarPorId(@PathVariable Long id) {
        return establecimientoServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar establecimientos por empresa (usando id de empresa)
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Establecimiento>> listarPorEmpresa(@PathVariable Long empresaId) {
        return empresaServicio.buscarPorId(empresaId)
                .map(empresa -> ResponseEntity.ok(
                        establecimientoServicio.listarPorEmpresa(empresa)
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear establecimiento (recibe un JSON con empresa.id)
    @PostMapping
    public ResponseEntity<Establecimiento> crear(@RequestBody Establecimiento establecimiento) {
        // Validar que la empresa exista
        Empresa empresa = establecimiento.getEmpresa();
        if (empresa == null || empresa.getId() == null ||
                empresaServicio.buscarPorId(empresa.getId()).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Establecimiento guardado = establecimientoServicio.guardar(establecimiento);
        return ResponseEntity.ok(guardado);
    }

    // Actualizar establecimiento
    @PutMapping("/{id}")
    public ResponseEntity<Establecimiento> actualizar(@PathVariable Long id,
                                                      @RequestBody Establecimiento establecimiento) {
        return establecimientoServicio.buscarPorId(id)
                .map(existente -> {
                    establecimiento.setId(id);
                    Establecimiento actualizado = establecimientoServicio.guardar(establecimiento);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar establecimiento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (establecimientoServicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        establecimientoServicio.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
