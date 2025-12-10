package com.factura.facturacion.servicios.sri;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;
import com.factura.facturacion.entidades.factura.Factura;

@Service
public class SriEnvioServicio {

    private final SriXmlBuilderServicio xmlBuilder;
    private final com.factura.facturacion.repositorios.FacturaRepositorio facturaRepositorio;

    public SriEnvioServicio(SriXmlBuilderServicio xmlBuilder,
            com.factura.facturacion.repositorios.FacturaRepositorio facturaRepositorio) {
        this.xmlBuilder = xmlBuilder;
        this.facturaRepositorio = facturaRepositorio;
    }

    /**
     * Simula el envío al SRI.
     * Guarda estado RECIBIDA y crea el archivo XML.
     */
    public String enviar(Factura factura) {
        try {
            String xml = xmlBuilder.construirXmlFactura(factura);

            // GUARDAR XML EN DISCO
            String nombreArchivo = (factura.getClaveAcceso() != null ? factura.getClaveAcceso()
                    : factura.getSecuencial()) + ".xml";
            Path rutaCarpeta = Paths.get("C:/Factura/xmls/");
            if (!Files.exists(rutaCarpeta)) {
                Files.createDirectories(rutaCarpeta);
            }
            Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);
            Files.writeString(rutaArchivo, xml);
            System.out.println(">> XML FACTURA GUARDADO EN: " + rutaArchivo.toString());

            // Simulación REMOVIDA: No cambiamos estado a RECIBIDA ni AUTORIZADA
            // factura.setEstado("RECIBIDA");
            // facturaRepositorio.save(factura);

            return "Factura " + factura.getId() + " XML Generado (Sin envío real).";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creando XML: " + e.getMessage();
        }
    }

    /**
     * Simula la autorización en el SRI.
     * (DESACTIVADO)
     */
    public String autorizar(Factura factura) {
        return "Autorización simulada desactivada.";
    }
}
