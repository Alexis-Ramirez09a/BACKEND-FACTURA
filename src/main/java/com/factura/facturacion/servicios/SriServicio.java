package com.factura.facturacion.servicios;

import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.repositorios.FacturaRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SriServicio {

    private final FacturaRepositorio facturaRepositorio;
    private final Random random = new Random();

    public SriServicio(FacturaRepositorio facturaRepositorio) {
        this.facturaRepositorio = facturaRepositorio;
    }

    public Factura enviarFactura(Long facturaId) throws InterruptedException {
        Factura factura = facturaRepositorio.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        // Simular generación de XML
        String xml = generarXmlSimulado(factura);
        System.out.println(">> ENVIANDO XML AL SRI: " + xml);

        // Simular tiempo de espera (1-3 seg)
        Thread.sleep(1000 + random.nextInt(2000));

        // Simular respuesta
        boolean autorizado = random.nextBoolean(); // 50/50 chance
        // O forzar autorizado para pruebas más amigables:
        // boolean autorizado = true;

        if (autorizado) {
            factura.setEstado("AUTORIZADA");
            factura.setFechaAutorizacion(LocalDateTime.now());
            System.out.println(">> FACTURA " + factura.getSecuencial() + " AUTORIZADA");
        } else {
            factura.setEstado("RECHAZADA");
            factura.setObservacion("Error simulado por SRI: RUC inválido o servicio no disponible");
            System.out.println(">> FACTURA " + factura.getSecuencial() + " RECHAZADA");
        }

        return facturaRepositorio.save(factura);
    }

    private String generarXmlSimulado(Factura factura) {
        return "<factura><infoTributaria><secuencial>" + factura.getSecuencial()
                + "</secuencial></infoTributaria></factura>";
    }
}
