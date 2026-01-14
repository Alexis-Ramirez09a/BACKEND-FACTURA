package com.factura.facturacion.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.empresa.Empresa;
import com.factura.facturacion.entidades.empresa.Establecimiento;

@Repository
public interface EstablecimientoRepositorio extends JpaRepository<Establecimiento, Long> {

    // Buscar todos los establecimientos por empresa
    List<Establecimiento> findByEmpresa(Empresa empresa);

    // Buscar por el código (001, 002, etc.)
    // Buscar por el código (001, 002, etc.)
    Establecimiento findByCodigo(String codigo);

    java.util.Optional<Establecimiento> findByCodigoAndEmpresa(String codigo, Empresa empresa);
}
// Permite buscar todos los establecimientos de una empresa
// Permite encontrar un establecimiento por su código
// Maneja CRUD automático