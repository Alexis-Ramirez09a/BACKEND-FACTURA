package com.factura.facturacion.util.sri;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ClaveAccesoUtil {

    // Genera clave de acceso de 49 dígitos según estructura SRI
    public static String generarClaveAcceso(
            LocalDate fechaEmision,
            String tipoComprobante,   // 01
            String ruc,
            String ambiente,          // 1 pruebas, 2 producción
            String serie,             // 001001 (establecimiento+punto)
            String secuencial,        // 000000123
            String codigoNumerico,    // 8 dígitos
            String tipoEmision        // 1 normal
    ) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("ddMMyyyy");
        String fecha = fechaEmision.format(fmt); // ddMMyyyy

        String base = fecha
                + tipoComprobante
                + ruc
                + ambiente
                + serie
                + secuencial
                + codigoNumerico
                + tipoEmision;

        int digitoVerificador = calcularDigitoVerificador(base);
        return base + digitoVerificador;
    }

    // Cálculo dígito verificador módulo 11
    private static int calcularDigitoVerificador(String cadena) {
        int[] pesos = {2, 3, 4, 5, 6, 7};
        int suma = 0;
        int pesoIndex = 0;

        for (int i = cadena.length() - 1; i >= 0; i--) {
            int numero = Character.getNumericValue(cadena.charAt(i));
            suma += numero * pesos[pesoIndex];
            pesoIndex = (pesoIndex + 1) % pesos.length;
        }

        int modulo = suma % 11;
        int digito = 11 - modulo;
        if (digito == 11) {
            return 0;
        } else if (digito == 10) {
            return 1;
        } else {
            return digito;
        }
    }
}
