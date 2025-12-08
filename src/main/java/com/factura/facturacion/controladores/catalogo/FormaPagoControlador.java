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

import com.factura.facturacion.entidades.catalogo.FormaPago;
import com.factura.facturacion.servicios.catalogo.FormaPagoServicio;

@RestController
@RequestMapping("/api/formas-pago")
public class FormaPagoControlador {

    private final FormaPagoServicio formaPagoServicio;

    public FormaPagoControlador(FormaPagoServicio formaPagoServicio) {
        this.formaPagoServicio = formaPagoServicio;
    }

    // Listar todas las formas de pago
    @GetMapping
    public List<FormaPago> listar() {
        return formaPagoServicio.listarTodas();
    }

    // Listar solo formas de pago activas
    @GetMapping("/activas")
    public List<FormaPago> listarActivas() {
        return formaPagoServicio.listarActivas();
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<FormaPago> buscarPorId(@PathVariable Long id) {
        return formaPagoServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar por código SRI
    @GetMapping("/codigo/{codigoSri}")
    public ResponseEntity<FormaPago> buscarPorCodigoSri(@PathVariable String codigoSri) {
        return formaPagoServicio.buscarPorCodigoSri(codigoSri)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nueva forma de pago
    @PostMapping
    public FormaPago crear(@RequestBody FormaPago formaPago) {
        return formaPagoServicio.guardar(formaPago);
    }

    // Actualizar forma de pago
    @PutMapping("/{id}")
    public ResponseEntity<FormaPago> actualizar(@PathVariable Long id,
                                                @RequestBody FormaPago formaPago) {
        return formaPagoServicio.buscarPorId(id)
                .map(existente -> {
                    formaPago.setId(id);
                    FormaPago actualizado = formaPagoServicio.guardar(formaPago);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar forma de pago
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (formaPagoServicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        formaPagoServicio.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
