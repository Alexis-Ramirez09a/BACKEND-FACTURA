package com.factura.facturacion.controladores.factura;

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

import com.factura.facturacion.entidades.factura.LogEnvioSri;
import com.factura.facturacion.servicios.factura.FacturaServicio;
import com.factura.facturacion.servicios.factura.LogEnvioSriServicio;

@RestController
@RequestMapping("/api/logs-envio-sri")
public class LogEnvioSriControlador {

    private final LogEnvioSriServicio logEnvioSriServicio;
    private final FacturaServicio facturaServicio;

    public LogEnvioSriControlador(LogEnvioSriServicio logEnvioSriServicio,
                                  FacturaServicio facturaServicio) {
        this.logEnvioSriServicio = logEnvioSriServicio;
        this.facturaServicio = facturaServicio;
    }

    // Listar todos los logs
    @GetMapping
    public List<LogEnvioSri> listar() {
        return logEnvioSriServicio.listarTodos();
    }

    // Buscar log por ID
    @GetMapping("/{id}")
    public ResponseEntity<LogEnvioSri> buscarPorId(@PathVariable Long id) {
        return logEnvioSriServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar logs por factura
    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<List<LogEnvioSri>> listarPorFactura(@PathVariable Long facturaId) {
        return facturaServicio.buscarPorId(facturaId)
                .map(factura -> ResponseEntity.ok(
                        logEnvioSriServicio.listarPorFactura(factura)
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar logs por estado (RECIBIDA, AUTORIZADA, RECHAZADA, ERROR)
    @GetMapping("/estado/{estado}")
    public List<LogEnvioSri> listarPorEstado(@PathVariable String estado) {
        return logEnvioSriServicio.listarPorEstado(estado);
    }

    // Crear log (normalmente lo usará internamente el sistema)
    @PostMapping
    public LogEnvioSri crear(@RequestBody LogEnvioSri log) {
        return logEnvioSriServicio.guardar(log);
    }

    // Actualizar log
    @PutMapping("/{id}")
    public ResponseEntity<LogEnvioSri> actualizar(@PathVariable Long id,
                                                  @RequestBody LogEnvioSri log) {
        return logEnvioSriServicio.buscarPorId(id)
                .map(existente -> {
                    log.setId(id);
                    LogEnvioSri actualizado = logEnvioSriServicio.guardar(log);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar log (en producción normalmente no se borra)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (logEnvioSriServicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        logEnvioSriServicio.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
