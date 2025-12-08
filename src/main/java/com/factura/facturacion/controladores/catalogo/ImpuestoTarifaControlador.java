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

import com.factura.facturacion.entidades.catalogo.ImpuestoTarifa;
import com.factura.facturacion.servicios.catalogo.ImpuestoServicio;
import com.factura.facturacion.servicios.catalogo.ImpuestoTarifaServicio;

@RestController
@RequestMapping("/api/impuestos-tarifas")
public class ImpuestoTarifaControlador {

    private final ImpuestoTarifaServicio impuestoTarifaServicio;
    private final ImpuestoServicio impuestoServicio;

    public ImpuestoTarifaControlador(ImpuestoTarifaServicio impuestoTarifaServicio,
                                     ImpuestoServicio impuestoServicio) {
        this.impuestoTarifaServicio = impuestoTarifaServicio;
        this.impuestoServicio = impuestoServicio;
    }

    // Listar todas las tarifas
    @GetMapping
    public List<ImpuestoTarifa> listar() {
        return impuestoTarifaServicio.listarTodas();
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<ImpuestoTarifa> buscarPorId(@PathVariable Long id) {
        return impuestoTarifaServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar tarifas por impuesto (ej: todas las del IVA)
    @GetMapping("/impuesto/{impuestoId}")
    public ResponseEntity<List<ImpuestoTarifa>> listarPorImpuesto(@PathVariable Long impuestoId) {
        return impuestoServicio.buscarPorId(impuestoId)
                .map(impuesto ->
                        ResponseEntity.ok(impuestoTarifaServicio.listarPorImpuesto(impuesto))
                )
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar tarifa específica por impuesto + códigoTarifa
    @GetMapping("/impuesto/{impuestoId}/codigo/{codigoTarifa}")
    public ResponseEntity<ImpuestoTarifa> buscarPorImpuestoYCodigo(@PathVariable Long impuestoId,
                                                                   @PathVariable String codigoTarifa) {
        return impuestoServicio.buscarPorId(impuestoId)
                .flatMap(impuesto -> impuestoTarifaServicio.buscarPorImpuestoYCodigo(impuesto, codigoTarifa))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nueva tarifa
    @PostMapping
    public ImpuestoTarifa crear(@RequestBody ImpuestoTarifa tarifa) {
        return impuestoTarifaServicio.guardar(tarifa);
    }

    // Actualizar tarifa
    @PutMapping("/{id}")
    public ResponseEntity<ImpuestoTarifa> actualizar(@PathVariable Long id,
                                                     @RequestBody ImpuestoTarifa tarifa) {
        return impuestoTarifaServicio.buscarPorId(id)
                .map(existente -> {
                    tarifa.setId(id);
                    ImpuestoTarifa actualizada = impuestoTarifaServicio.guardar(tarifa);
                    return ResponseEntity.ok(actualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar tarifa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (impuestoTarifaServicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        impuestoTarifaServicio.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
