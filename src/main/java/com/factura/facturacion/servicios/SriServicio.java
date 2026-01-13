
package com.factura.facturacion.servicios;

import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.repositorios.FacturaRepositorio;
import com.factura.facturacion.servicios.sri.SriAutorizacionServicio;
import com.factura.facturacion.servicios.sri.SriRecepcionService;
import com.factura.facturacion.servicios.sri.SriXmlBuilderServicio;
import com.factura.facturacion.servicios.sri.firma.FirmaStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class SriServicio {

    private final FacturaRepositorio facturaRepositorio;
    private final SriXmlBuilderServicio xmlBuilder;
    private final FirmaStrategy firmaStrategy;
    private final SriRecepcionService recepctionService;
    private final SriAutorizacionServicio autorizacionService;

    @Value("${firma.path}")
    private String firmaPath;

    @Value("${firma.clave}")
    private String firmaClave;

    public SriServicio(FacturaRepositorio facturaRepositorio,
            SriXmlBuilderServicio xmlBuilder,
            @Qualifier("firmaNoOpStrategy") FirmaStrategy firmaNoOp, // Default injection, logic below helps switch or
                                                                     // use @Conditional
            @Qualifier("firmaRealStrategy") FirmaStrategy firmaReal,
            @Value("${firma.estrategia}") String estrategia,
            SriRecepcionService recepctionService,
            SriAutorizacionServicio autorizacionService) {
        this.facturaRepositorio = facturaRepositorio;
        this.xmlBuilder = xmlBuilder;
        this.recepctionService = recepctionService;
        this.autorizacionService = autorizacionService;

        // Selección de estrategia en tiempo de ejecución (o construcción)
        if ("REAL".equalsIgnoreCase(estrategia)) {
            this.firmaStrategy = firmaReal;
        } else {
            this.firmaStrategy = firmaNoOp;
        }
    }

    public Factura enviarFactura(Long facturaId) {
        Factura factura = facturaRepositorio.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        try {
            System.out.println(">> 1. Generando XML...");
            String xmlContent = xmlBuilder.construirXmlFactura(factura);
            byte[] xmlBytes = xmlContent.getBytes(StandardCharsets.UTF_8);

            System.out.println(">> 2. Firmando XML (Estrategia seleccionada)...");
            byte[] xmlFirmado = firmaStrategy.firmar(xmlBytes, firmaPath, firmaClave);

            // Guardar XML firmado temporalmente para revisión
            Path rutaArchivo = guardarXmlFirmado(factura, xmlFirmado);
            System.out.println(">> XML Generado y Firmado en: " + rutaArchivo.toAbsolutePath());

            System.out.println(">> 3. Enviando al SRI (Recepción)...");
            // Nota: SriRecepcionService espera una ruta de archivo en su método actual
            // 'enviarFactura'.
            // Lo ideal sería refactorizarlo para aceptar bytes, pero usaremos la ruta por
            // ahora.
            String respuestaRecepcion = recepctionService.enviarFactura(rutaArchivo.toString());
            System.out.println(">> Respuesta Recepción: " + respuestaRecepcion);

            if (respuestaRecepcion.contains("RECIBIDA") || respuestaRecepcion.contains("CLAVE ACCESO REGISTRADA")) {
                // Si fue recibida, pedimos autorización
                System.out.println(">> 4. Solicitando Autorización...");
                Thread.sleep(2000); // SRI a veces demora en replicar
                String respuestaAutorizacion = autorizacionService.consultarAutorizacion(factura.getClaveAcceso());
                System.out.println(">> Respuesta Autorización: " + respuestaAutorizacion);

                if (respuestaAutorizacion.contains("AUTORIZADO")) {
                    factura.setEstado("AUTORIZADO");
                    factura.setFechaAutorizacion(LocalDateTime.now());
                } else {
                    factura.setEstado("RECHAZADA"); // O NO AUTORIZADO
                    factura.setObservacion("SRI: " + respuestaAutorizacion);
                }
            } else {
                factura.setEstado("DEVUELTA");
                factura.setObservacion("SRI Recepción: " + respuestaRecepcion);
            }

        } catch (Exception e) {
            e.printStackTrace();
            factura.setEstado("ERROR_SISTEMA");
            factura.setObservacion("Error: " + e.getMessage());
        }

        return facturaRepositorio.save(factura);
    }

    private Path guardarXmlFirmado(Factura factura, byte[] xmlFirmado) throws Exception {
        String nombreArchivo = (factura.getClaveAcceso() != null ? factura.getClaveAcceso()
                : "factura_" + factura.getSecuencial()) + ".xml";
        Path rutaCarpeta = Paths.get("C:/Factura/xmls_firmados/");
        if (!Files.exists(rutaCarpeta)) {
            Files.createDirectories(rutaCarpeta);
        }
        Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);
        Files.write(rutaArchivo, xmlFirmado);
        return rutaArchivo;
    }
}
