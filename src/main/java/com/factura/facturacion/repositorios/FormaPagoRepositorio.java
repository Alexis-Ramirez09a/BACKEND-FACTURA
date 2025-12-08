package com.factura.facturacion.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.catalogo.FormaPago;

@Repository
public interface FormaPagoRepositorio extends JpaRepository<FormaPago, Long> {

    // Buscar por el código SRI (01, 19, 20, etc)
    Optional<FormaPago> findByCodigoSri(String codigoSri);

    // Listar solo formas de pago activas
    List<FormaPago> findByActivoTrue();
}
//Permite buscar una forma de pago por su código SRI
//Permite listar todas las formas de pago que están activas
//Maneja CRUD automático