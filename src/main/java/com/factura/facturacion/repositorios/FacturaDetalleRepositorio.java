package com.factura.facturacion.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.entidades.factura.FacturaDetalle;

@Repository
public interface FacturaDetalleRepositorio extends JpaRepository<FacturaDetalle, Long> {

    // Obtener todos los detalles de una factura específica
    List<FacturaDetalle> findByFactura(Factura factura);
}
//Permite buscar todos los detalles asociados a una factura específica
//Maneja CRUD automático