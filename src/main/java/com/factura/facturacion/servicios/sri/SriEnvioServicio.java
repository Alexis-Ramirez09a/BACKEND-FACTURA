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
    private final com.factura.facturacion.servicios.sri.firma.FirmaStrategy firmaStrategy;

    @org.springframework.beans.factory.annotation.Value("${firma.path}")
    private String pathFirma;

    @org.springframework.beans.factory.annotation.Value("${firma.clave}")
    private String claveFirma;

    @org.springframework.beans.factory.annotation.Value("${firma.estrategia}")
    private String estrategiaFirma;

    public SriEnvioServicio(SriXmlBuilderServicio xmlBuilder,
            com.factura.facturacion.repositorios.FacturaRepositorio facturaRepositorio,
            SriRecepcionService sriRecepcionService,
            @org.springframework.beans.factory.annotation.Qualifier("firmaRealStrategy") com.factura.facturacion.servicios.sri.firma.FirmaStrategy firmaStrategy) {
        this.xmlBuilder = xmlBuilder;
        this.facturaRepositorio = facturaRepositorio;
        this.sriRecepcionService = sriRecepcionService;
        this.firmaStrategy = firmaStrategy;
    }

    /**
     * Envío REAL al SRI (Test Environment).
     * 1. Genera XML
     * 2. Firma XML (XAdES-BES)
     * 3. Envía a Recepción
     * 4. Si es RECIBIDA, consulta Autorización inmediatamente
     */
    public Factura enviar(Factura factura) {
        try {
            // OPTIMIZACIÓN: Si ya está EN_PROCESO, no re-enviamos ni re-generamos,
            // solo consultamos autorización usando el bucle de espera.
            if ("EN_PROCESO".equals(factura.getEstado()) && factura.getClaveAcceso() != null) {
                System.out.println(">> Factura EN_PROCESO. Consultando autorización (polling)...");
                intentarAutorizarRecursiva(factura);
                return facturaRepositorio.save(factura);
            }

            // 1. REGENERAR CLAVE ACCESO (Auto-Fix)
            // Si estamos re-enviando (no entró al bloque de EN_PROCESO), es vital
            // generar una nueva clave para que el SRI lo trate como un intento nuevo
            // y no choque con errores previos cacheados.
            if (factura.getClaveAcceso() != null) {
                System.out.println(">> Regenerando Clave de Acceso para reintento limpio...");
                regenerarClaveAcceso(factura);
                factura = facturaRepositorio.save(factura);
            }

            // 2. Generar XML
            System.out.println(">> Generando XML sin firmar...");
            String xml = xmlBuilder.construirXmlFactura(factura);
            System.out.println(">> XML generado. Longitud: " + xml.length() + " caracteres");

            // 3. Firmar XML
            byte[] xmlFirmadoBytes;
            System.out.println(">> Estrategia de firma: " + estrategiaFirma);
            System.out.println(">> Path firma: " + pathFirma);

            if ("REAL".equalsIgnoreCase(estrategiaFirma)) {
                System.out.println(">> Iniciando firma electrónica...");
                try {
                    xmlFirmadoBytes = firmaStrategy.firmar(xml.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                            pathFirma,
                            claveFirma);
                    System.out.println(">> ✓ XML Firmado. Tamaño: " + xmlFirmadoBytes.length + " bytes");
                } catch (Exception e) {
                    System.err.println(">> ✗ ERROR AL FIRMAR:");
                    e.printStackTrace();
                    throw new RuntimeException("Error al firmar XML: " + e.getMessage(), e);
                }
            } else {
                xmlFirmadoBytes = xml.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                System.out.println(">> MODO MOCK/DEV: XML NO firmado.");
            }

            // Guardar XML
            String nombreArchivo = (factura.getClaveAcceso() != null ? factura.getClaveAcceso()
                    : factura.getSecuencial()) + ".xml";
            Path rutaCarpeta = Paths.get("C:/Factura/xmls/");
            if (!Files.exists(rutaCarpeta)) {
                Files.createDirectories(rutaCarpeta);
            }
            Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);
            Files.write(rutaArchivo, xmlFirmadoBytes);

            // VERIFICACIÓN: Confirmar que el XML contiene la firma
            String xmlFirmadoStr = new String(xmlFirmadoBytes, java.nio.charset.StandardCharsets.UTF_8);
            if (xmlFirmadoStr.contains("<Signature") && xmlFirmadoStr.contains("</Signature>")) {
                System.out.println(">> ✓ FIRMA ELECTRÓNICA VERIFICADA en el XML");
                System.out
                        .println(">> XML contiene " + xmlFirmadoStr.split("<Signature").length + " bloque(s) de firma");
            } else {
                System.err.println(">> ⚠ ADVERTENCIA: XML NO contiene bloque <Signature>");
                System.err.println(">> Primeros 300 caracteres:");
                System.err.println(xmlFirmadoStr.substring(0, Math.min(300, xmlFirmadoStr.length())));
            }

            System.out.println(">> XML FACTURA GUARDADO EN: " + rutaArchivo.toString());

            // 4. ENVIAR A RECEPCIÓN (REAL)
            String respuestaRecepcion = sriRecepcionService.enviarFactura(rutaArchivo.toString());
            System.out.println(">> RESPUESTA SRI RECEPCION: " + respuestaRecepcion);

            // 5. Analizar respuesta de Recepción
            if (respuestaRecepcion.contains("RECIBIDA") || respuestaRecepcion.contains("CLAVE ACCESO REGISTRADA")
                    || respuestaRecepcion.contains("70") && respuestaRecepcion.contains("EN PROCESAMIENTO")) {

                // Si fue recibida, intentamos verificar AUTORIZACIÓN repetidamente
                intentarAutorizarRecursiva(factura);

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

    private void regenerarClaveAcceso(Factura factura) {
        try {
            String serie = factura.getCodigoEstablecimiento() + factura.getCodigoPuntoEmision();
            // Generar nuevo código numérico aleatorio
            String codigoNumerico = String.format("%08d", new java.util.Random().nextInt(100000000));

            String nuevaClave = com.factura.facturacion.util.sri.ClaveAccesoUtil.generarClaveAcceso(
                    factura.getFechaEmision(),
                    factura.getTipoComprobante(),
                    factura.getEmpresa().getRuc(),
                    factura.getEmpresa().getAmbiente(),
                    serie,
                    factura.getSecuencial(),
                    codigoNumerico,
                    factura.getEmpresa().getTipoEmision());
            factura.setClaveAcceso(nuevaClave);
        } catch (Exception e) {
            System.err.println("Error regenerando clave acceso: " + e.getMessage());
        }
    }

    /**
     * Intenta consultar la autorización múltiples veces (Polling).
     * NUEVA ESTRATEGIA: Intervalos más cortos (1s) para respuesta "de una".
     * Límite: 20-30 segundos máximo.
     */
    private void intentarAutorizarRecursiva(Factura factura) throws Exception {
        String respuestaAutorizacion = "";
        boolean yaAutorizado = false;

        // OPTIMIZACIÓN: Polling rápido para respuesta "inmediata" (< 2.5s)
        // Intentamos 5 veces con pausas de 400ms = ~2 segundos (+ overhead de red)
        int MAX_INTENTOS = 5;

        for (int i = 0; i < MAX_INTENTOS; i++) {
            System.out.println(">> Intento de autorización #" + (i + 1));
            respuestaAutorizacion = sriRecepcionService.consultarAutorizacion(factura.getClaveAcceso());

            // DEBUG: Ver qué responde exactamente
            if (respuestaAutorizacion.length() > 200) {
                System.out.println(">> Res: " + respuestaAutorizacion.substring(0, 200) + "...");
            } else {
                System.out.println(">> Res: " + respuestaAutorizacion);
            }

            if (respuestaAutorizacion.contains("AUTORIZADO") && !respuestaAutorizacion.contains("NO AUTORIZADO")) {
                factura.setEstado("AUTORIZADO");
                factura.setFechaAutorizacion(java.time.LocalDateTime.now());
                factura.setMensajeError(null);
                yaAutorizado = true;
                break;
            } else if (respuestaAutorizacion.contains("NO AUTORIZADO") || respuestaAutorizacion.contains("DEVUELTA")) {
                factura.setEstado("RECHAZADA");
                factura.setMensajeError(respuestaAutorizacion);
                yaAutorizado = true;
                break;
            } else if (respuestaAutorizacion.equals("EN_PROCESO")) {
                // SRI dice explícitamente que no está listo.
                // Seguimos esperando en el bucle.
                System.out.println(">> SRI: Comprobante aún en procesamiento...");
            }

            // Si no, esperamos brevemente (fast poll)
            if (i < (MAX_INTENTOS - 1)) {
                Thread.sleep(400); // 400ms * 5 = 2.0s
            }
        }

        if (!yaAutorizado) {
            // Si pasaron los intentos y nada...
            // NUEVA LÓGICA: Marcamos como EN_PROCESO para evitar timeout en frontend.
            // El usuario podrá consultar manualmente con el botón "Actualizar Estado".
            factura.setEstado("EN_PROCESO");
            factura.setMensajeError("En espera del SRI. Consulte nuevamente en unos momentos.");
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
