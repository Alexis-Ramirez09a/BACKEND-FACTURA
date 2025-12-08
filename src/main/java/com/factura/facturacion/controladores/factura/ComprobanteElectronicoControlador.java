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

import com.factura.facturacion.entidades.factura.ComprobanteElectronico;
import com.factura.facturacion.servicios.factura.ComprobanteElectronicoServicio;
import com.factura.facturacion.servicios.factura.FacturaServicio;

@RestController
@RequestMapping("/api/comprobantes-electronicos")
public class ComprobanteElectronicoControlador {

    private final ComprobanteElectronicoServicio comprobanteServicio;
    private final FacturaServicio facturaServicio;

    public ComprobanteElectronicoControlador(ComprobanteElectronicoServicio comprobanteServicio,
                                             FacturaServicio facturaServicio) {
        this.comprobanteServicio = comprobanteServicio;
        this.facturaServicio = facturaServicio;
    }

    // Listar todos los comprobantes electrónicos
    @GetMapping
    public List<ComprobanteElectronico> listar() {
        return comprobanteServicio.listarTodos();
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<ComprobanteElectronico> buscarPorId(@PathVariable Long id) {
        return comprobanteServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar comprobante por factura (id de factura)
    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<ComprobanteElectronico> buscarPorFactura(@PathVariable Long facturaId) {
        return facturaServicio.buscarPorId(facturaId)
                .flatMap(comprobanteServicio::buscarPorFactura)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar por clave de acceso
    @GetMapping("/clave-acceso/{claveAcceso}")
    public ResponseEntity<ComprobanteElectronico> buscarPorClaveAcceso(@PathVariable String claveAcceso) {
        return comprobanteServicio.buscarPorClaveAcceso(claveAcceso)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar por número de autorización
    @GetMapping("/autorizacion/{numeroAutorizacion}")
    public ResponseEntity<ComprobanteElectronico> buscarPorNumeroAutorizacion(
            @PathVariable String numeroAutorizacion) {
        return comprobanteServicio.buscarPorNumeroAutorizacion(numeroAutorizacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear comprobante (por ahora simple)
    @PostMapping
    public ComprobanteElectronico crear(@RequestBody ComprobanteElectronico comprobante) {
        return comprobanteServicio.guardar(comprobante);
    }

    // Actualizar comprobante
    @PutMapping("/{id}")
    public ResponseEntity<ComprobanteElectronico> actualizar(@PathVariable Long id,
                                                             @RequestBody ComprobanteElectronico comprobante) {
        return comprobanteServicio.buscarPorId(id)
                .map(existente -> {
                    comprobante.setId(id);
                    ComprobanteElectronico actualizado = comprobanteServicio.guardar(comprobante);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar comprobante
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (comprobanteServicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        comprobanteServicio.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
