package com.factura.facturacion.servicios.factura;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.catalogo.Cliente;
import com.factura.facturacion.entidades.empresa.Establecimiento;
import com.factura.facturacion.entidades.empresa.PuntoEmision;
import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.repositorios.FacturaRepositorio;

@Service
public class FacturaServicio {

    private final FacturaRepositorio facturaRepositorio;

    public FacturaServicio(FacturaRepositorio facturaRepositorio) {
        this.facturaRepositorio = facturaRepositorio;
    }

    // Listar todas las facturas
    public List<Factura> listarTodas() {
        return facturaRepositorio.findAll();
    }

    // Buscar por ID
    public Optional<Factura> buscarPorId(Long id) {
        return facturaRepositorio.findById(id);
    }

    // Buscar por número de factura (001-001-000000123)
    public Optional<Factura> buscarPorNumero(String codigoEstablecimiento,
                                             String codigoPuntoEmision,
                                             String secuencial) {
        return facturaRepositorio
                .findByCodigoEstablecimientoAndCodigoPuntoEmisionAndSecuencial(
                        codigoEstablecimiento,
                        codigoPuntoEmision,
                        secuencial
                );
    }

    // Listar facturas por cliente
    public List<Factura> listarPorCliente(Cliente cliente) {
        return facturaRepositorio.findByCliente(cliente);
    }

    // Listar facturas por rango de fechas
    public List<Factura> listarPorRangoFechas(LocalDate desde, LocalDate hasta) {
        return facturaRepositorio.findByFechaEmisionBetween(desde, hasta);
    }

    // Listar facturas por estado (PENDIENTE, AUTORIZADA, RECHAZADA, ANULADA)
    public List<Factura> listarPorEstado(String estado) {
        return facturaRepositorio.findByEstado(estado);
    }

    // Listar facturas por establecimiento y rango de fechas
    public List<Factura> listarPorEstablecimientoYRangoFechas(Establecimiento establecimiento,
                                                              LocalDate desde,
                                                              LocalDate hasta) {
        return facturaRepositorio
                .findByEstablecimientoAndFechaEmisionBetween(establecimiento, desde, hasta);
    }

    // Listar por establecimiento, punto de emisión y rango de fechas
    public List<Factura> listarPorEstablecimientoPuntoYRangoFechas(Establecimiento establecimiento,
                                                                   PuntoEmision puntoEmision,
                                                                   LocalDate desde,
                                                                   LocalDate hasta) {
        return facturaRepositorio
                .findByEstablecimientoAndPuntoEmisionAndFechaEmisionBetween(
                        establecimiento,
                        puntoEmision,
                        desde,
                        hasta
                );
    }

    // Guardar o actualizar una factura
    public Factura guardar(Factura factura) {
        // Más adelante aquí podemos llamar a un método para calcular totales antes de guardar
        return facturaRepositorio.save(factura);
    }

    // Cambiar estado de la factura (por ejemplo para anular)
    public Factura cambiarEstado(Factura factura, String nuevoEstado) {
        factura.setEstado(nuevoEstado);
        return facturaRepositorio.save(factura);
    }

    // Eliminar (no es recomendable en producción, mejor marcar como ANULADA)
    public void eliminarPorId(Long id) {
        facturaRepositorio.deleteById(id);
    }
}
//gestiona la lógica de negocio relacionada con los secuenciales de documentos, como facturas, notas de crédito, etc. Proporciona métodos para buscar, generar y crear secuenciales asociados a establecimientos y puntos de emisión específicos.