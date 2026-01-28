package com.factura.facturacion.repositorios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.catalogo.Cliente;
import com.factura.facturacion.entidades.empresa.Establecimiento;
import com.factura.facturacion.entidades.empresa.PuntoEmision;
import com.factura.facturacion.entidades.factura.Factura;

@Repository
public interface FacturaRepositorio extends JpaRepository<Factura, Long> {

        // Buscar por establecimiento + punto de emisión + secuencial (número único de
        // factura)
        Optional<Factura> findByCodigoEstablecimientoAndCodigoPuntoEmisionAndSecuencial(
                        String codigoEstablecimiento,
                        String codigoPuntoEmision,
                        String secuencial);

        // Buscar por cliente
        List<Factura> findByCliente(Cliente cliente);

        // Buscar por usuario
        List<Factura> findByUsuario(com.factura.facturacion.entidades.seguridad.Usuario usuario);

        // Buscar por rango de fechas
        List<Factura> findByFechaEmisionBetween(LocalDate desde, LocalDate hasta);

        // Buscar por estado (PENDIENTE, AUTORIZADA, etc.)
        List<Factura> findByEstado(String estado);

        // Buscar por establecimiento y fecha
        List<Factura> findByEstablecimientoAndFechaEmisionBetween(
                        Establecimiento establecimiento,
                        LocalDate desde,
                        LocalDate hasta);

        // Buscar por establecimiento + punto de emisión + rango de fechas
        List<Factura> findByEstablecimientoAndPuntoEmisionAndFechaEmisionBetween(
                        Establecimiento establecimiento,
                        PuntoEmision puntoEmision,
                        LocalDate desde,
                        LocalDate hasta);

        @org.springframework.data.jpa.repository.Query("SELECT SUM(f.importeTotal) FROM Factura f WHERE f.fechaEmision BETWEEN :desde AND :hasta AND f.estado != 'ANULADA'")
        java.math.BigDecimal sumTotalByFechaEmisionBetweenAndEstadoNot(
                        @org.springframework.data.repository.query.Param("desde") LocalDate desde,
                        @org.springframework.data.repository.query.Param("hasta") LocalDate hasta);
}
// Permite buscar facturas por varios criterios como establecimiento, punto de
// emisión, secuencial, cliente, rango de fechas y estado
// Maneja CRUD automático