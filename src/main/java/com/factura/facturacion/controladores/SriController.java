package com.factura.facturacion.controladores;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.factura.facturacion.servicios.sri.SriAutorizacionServicio;
import com.factura.facturacion.servicios.sri.SriRecepcionService;

@RestController
@RequestMapping("/api/sri-manual") // Changed path to avoid conflict with existing SriControlador
public class SriController {

    private final SriRecepcionService recepcionService;
    private final SriAutorizacionServicio autorizacionService;

    public SriController(SriRecepcionService recepcionService,
            SriAutorizacionServicio autorizacionService) {
        this.recepcionService = recepcionService;
        this.autorizacionService = autorizacionService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<?> enviar(@RequestParam String ruta) {
        try {
            String resultado = recepcionService.enviarFactura(ruta);
            return ResponseEntity.ok(Map.of("resultado", resultado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/autorizar")
    public ResponseEntity<?> autorizar(@RequestParam String claveAcceso) {
        try {
            String resultado = autorizacionService.consultarAutorizacion(claveAcceso);
            return ResponseEntity.ok(Map.of("resultado", resultado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
