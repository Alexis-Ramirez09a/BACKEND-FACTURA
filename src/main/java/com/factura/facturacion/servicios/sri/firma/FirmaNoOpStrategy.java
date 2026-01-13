package com.factura.facturacion.servicios.sri.firma;

import org.springframework.stereotype.Component;

@Component("firmaNoOpStrategy")
public class FirmaNoOpStrategy implements FirmaStrategy {

    @Override
    public byte[] firmar(byte[] xmlBytes, String pathFirma, String claveFirma) throws Exception {
        System.out.println(">> [MOCK] Firma electronica omitida (Estrategia NoOp). Retornando XML original.");
        return xmlBytes;
    }
}
