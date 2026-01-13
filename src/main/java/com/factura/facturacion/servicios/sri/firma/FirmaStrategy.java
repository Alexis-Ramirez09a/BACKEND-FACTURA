package com.factura.facturacion.servicios.sri.firma;

public interface FirmaStrategy {
    /**
     * Firma un documento XML en formato byte array.
     * 
     * @param xmlBytes   Contenido del XML
     * @param pathFirma  Ruta al archivo .p12
     * @param claveFirma Clave del archivo .p12
     * @return XML firmado en bytes
     */
    byte[] firmar(byte[] xmlBytes, String pathFirma, String claveFirma) throws Exception;
}
