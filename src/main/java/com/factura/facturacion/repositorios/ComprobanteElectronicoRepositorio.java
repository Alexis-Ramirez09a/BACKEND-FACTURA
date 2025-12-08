package com.factura.facturacion.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.factura.ComprobanteElectronico;
import com.factura.facturacion.entidades.factura.Factura;

@Repository
public interface ComprobanteElectronicoRepositorio extends JpaRepository<ComprobanteElectronico, Long> {

    // Buscar comprobante por factura
    Optional<ComprobanteElectronico> findByFactura(Factura factura);

    // Buscar por clave de acceso
    Optional<ComprobanteElectronico> findByClaveAcceso(String claveAcceso);

    // Buscar por número de autorización
    Optional<ComprobanteElectronico> findByNumeroAutorizacion(String numeroAutorizacion);
}
//Permite buscar comprobantes electrónicos por factura, clave de acceso o número de autorización
//Maneja CRUD automático