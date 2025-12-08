package com.factura.facturacion.servicios.sri;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class SriClienteServicio {

    // Endpoints Pruebas
    private static final String URL_RECEPCION = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline";
    private static final String URL_AUTORIZACION = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline";

    private final HttpClient httpClient;

    public SriClienteServicio() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String enviarRecepcion(byte[] xmlFirmado) throws Exception {
        String xmlBase64 = Base64.getEncoder().encodeToString(xmlFirmado);

        String soapEnvelope = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ec="http://ec.gob.sri.ws.recepcion">
                    <soapenv:Header/>
                    <soapenv:Body>
                        <ec:validarComprobante>
                            <xml>%s</xml>
                        </ec:validarComprobante>
                    </soapenv:Body>
                </soapenv:Envelope>
                """
                .formatted(xmlBase64);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_RECEPCION))
                .header("Content-Type", "text/xml; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(soapEnvelope, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String consultarAutorizacion(String claveAcceso) throws Exception {
        String soapEnvelope = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ec="http://ec.gob.sri.ws.autorizacion">
                    <soapenv:Header/>
                    <soapenv:Body>
                        <ec:autorizacionComprobante>
                            <claveAccesoComprobante>%s</claveAccesoComprobante>
                        </ec:autorizacionComprobante>
                    </soapenv:Body>
                </soapenv:Envelope>
                """
                .formatted(claveAcceso);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_AUTORIZACION))
                .header("Content-Type", "text/xml; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(soapEnvelope, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
