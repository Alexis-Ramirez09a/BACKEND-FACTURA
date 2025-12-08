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
import com.factura.facturacion.servicios.empresa.EmpresaServicio;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaControlador {

    private final EmpresaServicio empresaServicio;

    public EmpresaControlador(EmpresaServicio empresaServicio) {
        this.empresaServicio = empresaServicio;
    }

    // Listar todas las empresas
    @GetMapping
    public List<Empresa> listar() {
        return empresaServicio.listarTodas();
    }

    // Buscar una empresa por id
    @GetMapping("/{id}")
    public ResponseEntity<Empresa> buscarPorId(@PathVariable Long id) {
        return empresaServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar por RUC
    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<Empresa> buscarPorRuc(@PathVariable String ruc) {
        return empresaServicio.buscarPorRuc(ruc)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nueva empresa
    @PostMapping
    public Empresa crear(@RequestBody Empresa empresa) {
        return empresaServicio.guardar(empresa);
    }

    // Actualizar empresa
    @PutMapping("/{id}")
    public ResponseEntity<Empresa> actualizar(@PathVariable Long id,
                                              @RequestBody Empresa empresa) {
        return empresaServicio.buscarPorId(id)
                .map(existente -> {
                    empresa.setId(id); // aseguramos que se actualice la correcta
                    Empresa actualizada = empresaServicio.guardar(empresa);
                    return ResponseEntity.ok(actualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar empresa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (empresaServicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        empresaServicio.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
