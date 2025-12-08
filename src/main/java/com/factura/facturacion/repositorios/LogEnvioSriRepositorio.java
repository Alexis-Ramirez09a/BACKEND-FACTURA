package com.factura.facturacion.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.entidades.factura.LogEnvioSri;

@Repository
public interface LogEnvioSriRepositorio extends JpaRepository<LogEnvioSri, Long> {

    // Todos los logs de una factura (recibido, autorizado, rechazado, error)
    List<LogEnvioSri> findByFactura(Factura factura);

    // Buscar por estado (RECIBIDA, AUTORIZADA, RECHAZADA, ERROR)
    List<LogEnvioSri> findByEstado(String estado);
}
//Permite buscar todos los logs asociados a una factura específica
//Permite buscar logs por estado específico
//Maneja CRUD automático