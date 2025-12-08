package com.factura.facturacion.servicios.factura;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.entidades.factura.FacturaDetalle;
import com.factura.facturacion.repositorios.FacturaDetalleRepositorio;

@Service
public class FacturaDetalleServicio {

    private final FacturaDetalleRepositorio facturaDetalleRepositorio;

    public FacturaDetalleServicio(FacturaDetalleRepositorio facturaDetalleRepositorio) {
        this.facturaDetalleRepositorio = facturaDetalleRepositorio;
    }

    public List<FacturaDetalle> listarTodos() {
        return facturaDetalleRepositorio.findAll();
    }

    public Optional<FacturaDetalle> buscarPorId(Long id) {
        return facturaDetalleRepositorio.findById(id);
    }

    public List<FacturaDetalle> listarPorFactura(Factura factura) {
        return facturaDetalleRepositorio.findByFactura(factura);
    }

    public FacturaDetalle guardar(FacturaDetalle detalle) {
        return facturaDetalleRepositorio.save(detalle);
    }

    public void eliminarPorId(Long id) {
        facturaDetalleRepositorio.deleteById(id);
    }
}
//gestiona la lógica de negocio relacionada con los secuenciales de documentos, como facturas, notas de crédito, etc. Proporciona métodos para buscar, generar y crear secuenciales asociados a establecimientos y puntos de emisión específicos.