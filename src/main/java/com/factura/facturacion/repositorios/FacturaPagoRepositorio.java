package com.factura.facturacion.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.entidades.factura.FacturaPago;

@Repository
public interface FacturaPagoRepositorio extends JpaRepository<FacturaPago, Long> {

    // Listar todos los pagos de una factura
    List<FacturaPago> findByFactura(Factura factura);
}
//Permite buscar todos los pagos asociados a una factura específica
//Maneja CRUD automático