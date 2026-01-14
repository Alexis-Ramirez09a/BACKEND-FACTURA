package com.factura.facturacion.servicios.sri;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;
import com.factura.facturacion.entidades.factura.Factura;
// SriRecepcionService is in the same package, no import needed

@Service
public class SriEnvioServicio {

    private final SriXmlBuilderServicio xmlBuilder;
    private final com.factura.facturacion.repositorios.FacturaRepositorio facturaRepositorio;
    private final SriRecepcionService sriRecepcionService;

    public SriEnvioServicio(SriXmlBuilderServicio xmlBuilder,
            com.factura.facturacion.repositorios.FacturaRepositorio facturaRepositorio,
            SriRecepcionService sriRecepcionService) {
        this.xmlBuilder = xmlBuilder;
        this.facturaRepositorio = facturaRepositorio;
        this.sriRecepcionService = sriRecepcionService;
    }

    /**
     * Envío REAL al SRI (Test Environment).
     * 1. Genera XML
     * 2. Envía a Recepción
     * 3. Si es RECIBIDA, consulta Autorización inmediatamente (o el usuario lo hace
     * luego, pero el usuario pidió inmediato)
     */
    public Factura enviar(Factura factura) {
        try {
            // 1. Generar XML (Ya firmado si la firma es REAL, pero aquí solo construimos)
            // NOTA: El xmlBuilder debería firmarlo. Si es MOCK, el SRI lo rechazará.
            // Asumimos que el usuario quiere el INTENTO real.
            String xml = xmlBuilder.construirXmlFactura(factura);

            // Guardar XML
            String nombreArchivo = (factura.getClaveAcceso() != null ? factura.getClaveAcceso()
                    : factura.getSecuencial()) + ".xml";
            Path rutaCarpeta = Paths.get("C:/Factura/xmls/");
            if (!Files.exists(rutaCarpeta)) {
                Files.createDirectories(rutaCarpeta);
            }
            Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);
            Files.writeString(rutaArchivo, xml);
            System.out.println(">> XML FACTURA GUARDADO EN: " + rutaArchivo.toString());

            // 2. ENVIAR A RECEPCIÓN (REAL)
            String respuestaRecepcion = sriRecepcionService.enviarFactura(rutaArchivo.toString());
            System.out.println(">> RESPUESTA SRI RECEPCION: " + respuestaRecepcion);

            // 3. Analizar respuesta de Recepción
            if (respuestaRecepcion.contains("RECIBIDA")) {
                // Si fue recibida, intentamos verificar AUTORIZACIÓN inmediatamente
                // (El SRI a veces tarda unos segundos, así que podríamos esperar un poco o
                // intentar)
                Thread.sleep(3000); // Esperar 3 segundos para dar tiempo al SRI
                String respuestaAutorizacion = sriRecepcionService.consultarAutorizacion(factura.getClaveAcceso());

                if (respuestaAutorizacion.contains("AUTORIZADO") && !respuestaAutorizacion.contains("NO AUTORIZADO")) {
                    factura.setEstado("AUTORIZADO");
                    factura.setFechaAutorizacion(java.time.LocalDateTime.now());
                    factura.setMensajeError(null);
                } else {
                    // Puede ser NO AUTORIZADO o EN PROCESAMIENTO
                    factura.setEstado("RECHAZADA"); // O PENDIENTE si es 'EN PROCESO'
                    // Si dice NO AUTORIZADO, guardamos el error
                    factura.setMensajeError(respuestaAutorizacion);
                }

            } else {
                // DEVUELTA o FALLA
                factura.setEstado("RECHAZADA");
                factura.setMensajeError(respuestaRecepcion);
            }

            return facturaRepositorio.save(factura);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en envío SRI: " + e.getMessage());
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
