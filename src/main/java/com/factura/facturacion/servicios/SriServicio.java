package com.factura.facturacion.servicios;

import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.repositorios.FacturaRepositorio;
import com.factura.facturacion.servicios.sri.SriXmlBuilderServicio;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SriServicio {

    private final FacturaRepositorio facturaRepositorio;
    private final SriXmlBuilderServicio xmlBuilder;
    private final Random random = new Random();

    public SriServicio(FacturaRepositorio facturaRepositorio, SriXmlBuilderServicio xmlBuilder) {
        this.facturaRepositorio = facturaRepositorio;
        this.xmlBuilder = xmlBuilder;
    }

    public Factura enviarFactura(Long facturaId) throws InterruptedException {
        Factura factura = facturaRepositorio.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        try {
            System.out.println(">> INICIANDO SIMULACION DE ENVIO AL SRI: ");

            // 1. Generar XML usando el servicio
            String xmlContent = xmlBuilder.construirXmlFactura(factura);

            // 2. Definir nombre y ruta del archivo
            String nombreArchivo = factura.getSecuencial() + ".xml";
            // Si la factura tiene clave de acceso, es mejor usarla:
            if (factura.getClaveAcceso() != null) {
                nombreArchivo = factura.getClaveAcceso() + ".xml";
            }

            Path rutaCarpeta = Paths.get("C:/Factura/xmls/");
            if (!Files.exists(rutaCarpeta)) {
                Files.createDirectories(rutaCarpeta);
            }

            Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);

            // 3. Guardar el archivo
            Files.writeString(rutaArchivo, xmlContent);
            System.out.println(">> DOC ELECTRÓNICO GENERADO: " + rutaArchivo.toString());

            // 4. Simular respuesta positiva del SRI
            Thread.sleep(1000); // Esperar un segundo

            factura.setEstado("AUTORIZADA");
            factura.setFechaAutorizacion(LocalDateTime.now());
            // factura.setRutaXml(rutaArchivo.toString()); // Descomentar si tienes este
            // campo

        } catch (Exception e) {
            e.printStackTrace();
            factura.setEstado("RECHAZADA");
            factura.setObservacion("Error simulado: " + e.getMessage());
        }

        return facturaRepositorio.save(factura);
    }
}
