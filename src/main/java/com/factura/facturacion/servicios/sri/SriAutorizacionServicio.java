package com.factura.facturacion.servicios.sri;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class SriAutorizacionServicio {

    // FIX: Use Offline URL (WSDL) correctly
    private static final String SRI_AUTORIZACION = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

    public String consultarAutorizacion(String claveAcceso) throws Exception {

        String soap = """
                    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                      xmlns:ec="http://ec.gob.sri.ws.autorizacion">
                        <soapenv:Header/>
                        <soapenv:Body>
                            <ec:autorizacionComprobante>
                                <claveAccesoComprobante>%s</claveAccesoComprobante>
                            </ec:autorizacionComprobante>
                        </soapenv:Body>
                    </soapenv:Envelope>
                """.formatted(claveAcceso);

        URL url = new URL(SRI_AUTORIZACION);
        java.net.URLConnection connection = url.openConnection();

        // SSL PATCH: To avoid "No subject alternative names matching IP"
        if (connection instanceof javax.net.ssl.HttpsURLConnection) {
            ((javax.net.ssl.HttpsURLConnection) connection).setHostnameVerifier((hostname, session) -> true);
        }

        HttpURLConnection conn = (HttpURLConnection) connection;
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

        conn.getOutputStream().write(soap.getBytes());

        String response = new String(conn.getInputStream().readAllBytes());

        return procesarRespuestaAutorizacion(response);
    }

    // Procesar estado AUTORIZADO / NO AUTORIZADO / EN PROCESAMIENTO
    private String procesarRespuestaAutorizacion(String xml) {

        if (xml.contains("<estado>AUTORIZADO</estado>")) {
            return "AUTORIZADO";
        }

        if (xml.contains("<estado>NO AUTORIZADO</estado>")) {

            StringBuilder errores = new StringBuilder("NO AUTORIZADO:\n");
            Pattern p = Pattern.compile("<mensaje>(.*?)</mensaje>");
            Matcher m = p.matcher(xml);

            while (m.find()) {
                errores.append("- ").append(m.group(1)).append("\n");
            }

            return errores.toString();
        }

        // Si no hay autorizaciones y no es error explícito, sigue EN PROCESO
        if (xml.contains("<numeroAutorizaciones>0</numeroAutorizaciones>")) {
            return "EN_PROCESO";
        }

        return "Respuesta desconocida:\n" + xml;
    }
}
