package com.factura.facturacion.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.factura.FacturaDetalle;
import com.factura.facturacion.entidades.factura.FacturaDetalleImpuesto;

@Repository
public interface FacturaDetalleImpuestoRepositorio extends JpaRepository<FacturaDetalleImpuesto, Long> {

    // Buscar todos los impuestos de un detalle específico
    List<FacturaDetalleImpuesto> findByDetalle(FacturaDetalle detalle);
}
//Permite buscar todos los impuestos asociados a un detalle de factura específico
//Maneja CRUD automático