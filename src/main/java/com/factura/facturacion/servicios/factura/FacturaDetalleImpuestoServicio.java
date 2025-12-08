package com.factura.facturacion.servicios.factura;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.factura.FacturaDetalle;
import com.factura.facturacion.entidades.factura.FacturaDetalleImpuesto;
import com.factura.facturacion.repositorios.FacturaDetalleImpuestoRepositorio;

@Service
public class FacturaDetalleImpuestoServicio {

    private final FacturaDetalleImpuestoRepositorio detalleImpuestoRepositorio;

    public FacturaDetalleImpuestoServicio(FacturaDetalleImpuestoRepositorio detalleImpuestoRepositorio) {
        this.detalleImpuestoRepositorio = detalleImpuestoRepositorio;
    }

    public List<FacturaDetalleImpuesto> listarTodos() {
        return detalleImpuestoRepositorio.findAll();
    }

    public Optional<FacturaDetalleImpuesto> buscarPorId(Long id) {
        return detalleImpuestoRepositorio.findById(id);
    }

    public List<FacturaDetalleImpuesto> listarPorDetalle(FacturaDetalle detalle) {
        return detalleImpuestoRepositorio.findByDetalle(detalle);
    }

    public FacturaDetalleImpuesto guardar(FacturaDetalleImpuesto detalleImpuesto) {
        return detalleImpuestoRepositorio.save(detalleImpuesto);
    }

    public void eliminarPorId(Long id) {
        detalleImpuestoRepositorio.deleteById(id);
    }
}
