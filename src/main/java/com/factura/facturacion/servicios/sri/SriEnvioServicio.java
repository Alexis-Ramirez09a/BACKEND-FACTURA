package com.factura.facturacion.servicios.sri;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.factura.Factura;

@Service
public class SriEnvioServicio {

    private final SriXmlBuilderServicio xmlBuilder;

    public SriEnvioServicio(SriXmlBuilderServicio xmlBuilder) {
        this.xmlBuilder = xmlBuilder;
    }

    /**
     * Simula el envío al SRI.
     * Por ahora solo construye el XML y devuelve un mensaje.
     * Más adelante aquí puedes firmar el XML y llamar al web service real.
     */
    public String enviar(Factura factura) {
        String xml = xmlBuilder.construirXmlFactura(factura);
        // Aquí iría la lógica real de SOAP al SRI
        return "Factura " + factura.getId() + " ENVIADA (simulado).\nXML generado:\n" + xml;
    }

    /**
     * Simula la autorización en el SRI.
     */
    public String autorizar(Factura factura) {
        // Aquí luego consumirías el WS de autorización del SRI
        return "Factura " + factura.getId() + " AUTORIZADA (simulado).";
    }
}
