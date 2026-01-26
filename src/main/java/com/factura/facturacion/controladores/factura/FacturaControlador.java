package com.factura.facturacion.controladores.factura;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.factura.facturacion.dtos.factura.FacturaCrearDto;
import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.servicios.catalogo.ClienteServicio;
import com.factura.facturacion.servicios.factura.FacturaEmisionServicio;
import com.factura.facturacion.servicios.factura.FacturaServicio;
import com.factura.facturacion.servicios.reportes.FacturaReporteServicio;
import com.factura.facturacion.servicios.sri.SriEnvioServicio;

@RestController
@RequestMapping("/api/facturas")
public class FacturaControlador {

    private final FacturaServicio facturaServicio;
    private final ClienteServicio clienteServicio;
    private final FacturaEmisionServicio facturaEmisionServicio;
    private final com.factura.facturacion.servicios.sri.SriXmlBuilderServicio sriXmlBuilderServicio;

    public FacturaControlador(FacturaServicio facturaServicio,
            ClienteServicio clienteServicio,
            FacturaEmisionServicio facturaEmisionServicio,
            com.factura.facturacion.servicios.sri.SriXmlBuilderServicio sriXmlBuilderServicio) {
        this.facturaServicio = facturaServicio;
        this.clienteServicio = clienteServicio;
        this.facturaEmisionServicio = facturaEmisionServicio;
        this.sriXmlBuilderServicio = sriXmlBuilderServicio;
    }

    // Listar todas las facturas
    @GetMapping
    public List<Factura> listar() {
        return facturaServicio.listarTodas();
    }

    // Buscar factura por ID
    @GetMapping("/{id}")
    public ResponseEntity<Factura> buscarPorId(@PathVariable Long id) {
        return facturaServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar factura por número (establecimiento + punto + secuencial)
    @GetMapping("/numero")
    public ResponseEntity<Factura> buscarPorNumero(
            @RequestParam("establecimiento") String establecimiento,
            @RequestParam("puntoEmision") String puntoEmision,
            @RequestParam("secuencial") String secuencial) {
        return facturaServicio.buscarPorNumero(establecimiento, puntoEmision, secuencial)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar facturas por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Factura>> listarPorCliente(@PathVariable Long clienteId) {
        return clienteServicio.buscarPorId(clienteId)
                .map(cliente -> ResponseEntity.ok(
                        facturaServicio.listarPorCliente(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar facturas por rango de fechas:
    // /api/facturas/rango?desde=2024-01-01&hasta=2024-01-31
    @GetMapping("/rango")
    public ResponseEntity<List<Factura>> listarPorRangoFechas(
            @RequestParam("desde") String desdeStr,
            @RequestParam("hasta") String hastaStr) {
        try {
            LocalDate desde = LocalDate.parse(desdeStr);
            LocalDate hasta = LocalDate.parse(hastaStr);
            return ResponseEntity.ok(facturaServicio.listarPorRangoFechas(desde, hasta));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Listar por estado (PENDIENTE, AUTORIZADA, RECHAZADA, ANULADA)
    @GetMapping("/estado/{estado}")
    public List<Factura> listarPorEstado(@PathVariable String estado) {
        return facturaServicio.listarPorEstado(estado);
    }

    // Crear factura simple (sin lógica de emisión)
    @PostMapping
    public Factura crear(@RequestBody Factura factura) {
        return facturaServicio.guardar(factura);
    }

    // Emitir factura completa (usa DTO y toda la lógica de emisión)
    @PostMapping("/emitir")
    public ResponseEntity<?> emitir(@RequestBody FacturaCrearDto dto) {
        try {
            Factura factura = facturaEmisionServicio.emitirFactura(dto);
            return ResponseEntity.ok(factura);
        } catch (RuntimeException e) {
            // PARA DEPURAR: devolvemos el mensaje de error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar factura
    @PutMapping("/{id}")
    public ResponseEntity<Factura> actualizar(@PathVariable Long id,
            @RequestBody Factura factura) {
        return facturaServicio.buscarPorId(id)
                .map(existente -> {
                    factura.setId(id);
                    Factura actualizada = facturaServicio.guardar(factura);
                    return ResponseEntity.ok(actualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Cambiar estado de la factura (por ejemplo ANULAR)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Factura> cambiarEstado(@PathVariable Long id,
            @RequestParam("estado") String estado) {
        return facturaServicio.buscarPorId(id)
                .map(factura -> ResponseEntity.ok(
                        facturaServicio.cambiarEstado(factura, estado)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint específico para ANULAR
    @PutMapping("/{id}/anular")
    public ResponseEntity<Factura> anular(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(facturaServicio.anular(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar factura (no recomendable en producción, pero útil en desarrollo)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (facturaServicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        facturaServicio.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @Autowired
    private FacturaReporteServicio reporteServicio;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargar(@PathVariable Long id) throws Exception {
        Factura f = facturaServicio.buscarPorId(id).orElseThrow();
        byte[] pdf = reporteServicio.generarPdf(f);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=factura.pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @Autowired
    private SriEnvioServicio sriEnvioServicio;

    @PostMapping("/{id}/enviar-sri")
    public ResponseEntity<Factura> enviarSri(@PathVariable Long id) {
        Factura f = facturaServicio.buscarPorId(id).orElseThrow();
        return ResponseEntity.ok(sriEnvioServicio.enviar(f));
    }

    @PostMapping("/{id}/autorizar-sri")
    public String autorizarSri(@PathVariable Long id) {
        Factura f = facturaServicio.buscarPorId(id).orElseThrow();
        return sriEnvioServicio.autorizar(f);
    }

    @GetMapping(value = "/{id}/xml", produces = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> verXml(@PathVariable Long id) {
        return facturaServicio.buscarPorId(id)
                .map(f -> ResponseEntity.ok(sriXmlBuilderServicio.construirXmlFactura(f)))
                .orElse(ResponseEntity.notFound().build());
    }

}
