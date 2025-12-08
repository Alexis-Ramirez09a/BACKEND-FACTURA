package com.factura.facturacion.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.catalogo.Impuesto;

@Repository
public interface ImpuestoRepositorio extends JpaRepository<Impuesto, Long> {

    // Buscar por código SRI del impuesto (2 = IVA, 3 = ICE, 5 = IRBPNR)
    Optional<Impuesto> findByCodigo(String codigo);
}
//Permite buscar un impuesto por su código SRI
//Maneja CRUD automático