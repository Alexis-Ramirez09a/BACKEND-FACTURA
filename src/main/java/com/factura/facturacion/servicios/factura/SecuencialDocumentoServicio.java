package com.factura.facturacion.servicios.factura;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.factura.facturacion.entidades.empresa.Establecimiento;
import com.factura.facturacion.entidades.empresa.PuntoEmision;
import com.factura.facturacion.entidades.factura.SecuencialDocumento;
import com.factura.facturacion.repositorios.SecuencialDocumentoRepositorio;

@Service
public class SecuencialDocumentoServicio {

    private final SecuencialDocumentoRepositorio secuencialRepositorio;

    public SecuencialDocumentoServicio(SecuencialDocumentoRepositorio secuencialRepositorio) {
        this.secuencialRepositorio = secuencialRepositorio;
    }

    // Obtener secuencial actual
    public Optional<SecuencialDocumento> buscar(
            String tipoComprobante,
            Establecimiento establecimiento,
            PuntoEmision puntoEmision
    ) {
        return secuencialRepositorio.findByTipoComprobanteAndEstablecimientoAndPuntoEmision(
                tipoComprobante,
                establecimiento,
                puntoEmision
        );
    }

    // Generar el siguiente secuencial
    @Transactional
    public String generarSiguienteSecuencial(
            String tipoComprobante,
            Establecimiento establecimiento,
            PuntoEmision puntoEmision
    ) {
        SecuencialDocumento secuencial = buscar(tipoComprobante, establecimiento, puntoEmision)
                .orElseThrow(() -> new RuntimeException("No existe secuencial configurado"));

        // Como ultimoSecuencial es Integer, usamos Integer
        Integer siguiente = secuencial.getUltimoSecuencial() + 1;
        secuencial.setUltimoSecuencial(siguiente);
        secuencialRepositorio.save(secuencial);

        // Sin "format:" → eso no existe en Java
        return String.format("%09d", siguiente); // 000000001
    }

    // Crear secuencial inicial si no existe
    public SecuencialDocumento crearInicial(
            String tipoComprobante,
            Establecimiento establecimiento,
            PuntoEmision puntoEmision
    ) {
        SecuencialDocumento sec = new SecuencialDocumento();
        sec.setTipoComprobante(tipoComprobante);
        sec.setEstablecimiento(establecimiento);
        sec.setPuntoEmision(puntoEmision);
        sec.setUltimoSecuencial(0);   // no 0L
        sec.setActivo(true);          // no "activo: true"

        return secuencialRepositorio.save(sec);
    }
}
