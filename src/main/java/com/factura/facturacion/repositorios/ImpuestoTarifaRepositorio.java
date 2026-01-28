package com.factura.facturacion.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.catalogo.Impuesto;
import com.factura.facturacion.entidades.catalogo.ImpuestoTarifa;

@Repository
public interface ImpuestoTarifaRepositorio extends JpaRepository<ImpuestoTarifa, Long> {

    // Todas las tarifas de un impuesto (por ejemplo todas las tarifas de IVA)
    List<ImpuestoTarifa> findByImpuesto(Impuesto impuesto);

    // Buscar una tarifa específica por códigoTarifa (ejemplo: 2 = IVA 12%)
    // Buscar una tarifa específica por códigoTarifa (ejemplo: 2 = IVA 12%) y
    // Impuesto
    Optional<ImpuestoTarifa> findByCodigoTarifaAndImpuesto(String codigoTarifa, Impuesto impuesto);

    // Buscar una tarifa por su porcentaje (para entrada dinámica)
    Optional<ImpuestoTarifa> findByImpuestoAndPorcentaje(Impuesto impuesto, java.math.BigDecimal porcentaje);
}
// Permite buscar todas las tarifas asociadas a un impuesto
// Permite encontrar una tarifa específica de un impuesto por su código de
// tarifa
// Maneja CRUD automático