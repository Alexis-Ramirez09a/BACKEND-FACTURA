package com.factura.facturacion.controladores;

import com.factura.facturacion.servicios.ReporteServicio;
import com.lowagie.text.DocumentException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class ReporteControlador {

    private final ReporteServicio reporteServicio;

    public ReporteControlador(ReporteServicio reporteServicio) {
        this.reporteServicio = reporteServicio;
    }

    @GetMapping("/clientes")
    public ResponseEntity<byte[]> reporteClientes() throws DocumentException {
        byte[] pdf = reporteServicio.generarReporteClientes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/productos")
    public ResponseEntity<byte[]> reporteProductos() throws DocumentException {
        byte[] pdf = reporteServicio.generarReporteProductos();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/facturas")
    public ResponseEntity<byte[]> reporteFacturas() throws DocumentException {
        byte[] pdf = reporteServicio.generarReporteFacturas();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facturas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
