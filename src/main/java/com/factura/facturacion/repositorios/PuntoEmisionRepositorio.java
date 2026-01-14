package com.factura.facturacion.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.empresa.Establecimiento;
import com.factura.facturacion.entidades.empresa.PuntoEmision;

@Repository
public interface PuntoEmisionRepositorio extends JpaRepository<PuntoEmision, Long> {

    // Todos los puntos de un establecimiento
    List<PuntoEmision> findByEstablecimiento(Establecimiento establecimiento);

    // Buscar por código dentro de un establecimiento
    // Buscar por código dentro de un establecimiento
    PuntoEmision findByEstablecimientoAndCodigo(Establecimiento establecimiento, String codigo);

    java.util.Optional<PuntoEmision> findByCodigoAndEstablecimiento(String codigo, Establecimiento establecimiento);
}
// Permite buscar todos los puntos de emisión de un establecimiento
// Permite encontrar un punto de emisión por su código dentro de un
// establecimiento
// Maneja CRUD automático