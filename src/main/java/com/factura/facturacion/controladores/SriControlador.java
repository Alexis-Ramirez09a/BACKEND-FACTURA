package com.factura.facturacion.controladores;

import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.servicios.SriServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sri")
public class SriControlador {

    private final SriServicio sriServicio;

    public SriControlador(SriServicio sriServicio) {
        this.sriServicio = sriServicio;
    }

    @PostMapping("/enviar/{facturaId}")
    public ResponseEntity<Factura> enviarFactura(@PathVariable Long facturaId) {
        try {
            Factura factura = sriServicio.enviarFactura(facturaId);
            return ResponseEntity.ok(factura);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
