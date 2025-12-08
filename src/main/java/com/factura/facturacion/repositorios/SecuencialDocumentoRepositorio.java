package com.factura.facturacion.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.empresa.Establecimiento;
import com.factura.facturacion.entidades.empresa.PuntoEmision;
import com.factura.facturacion.entidades.factura.SecuencialDocumento;

@Repository
public interface SecuencialDocumentoRepositorio extends JpaRepository<SecuencialDocumento, Long> {

    // Buscar secuencial por tipo de comprobante + establecimiento + punto de emisión
    Optional<SecuencialDocumento> findByTipoComprobanteAndEstablecimientoAndPuntoEmision(
            String tipoComprobante, 
            Establecimiento establecimiento, 
            PuntoEmision puntoEmision
    );
}
//Permite buscar un secuencial de documento por tipo de comprobante, establecimiento y punto de emisión
//Maneja CRUD automático 