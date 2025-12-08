package com.factura.facturacion.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.empresa.Empresa;

@Repository
public interface EmpresaRepositorio extends JpaRepository<Empresa, Long> {

    // Buscar empresa por RUC
    Empresa findByRuc(String ruc);
}
