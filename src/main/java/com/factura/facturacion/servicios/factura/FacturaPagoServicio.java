package com.factura.facturacion.servicios.factura;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.entidades.factura.FacturaPago;
import com.factura.facturacion.repositorios.FacturaPagoRepositorio;

@Service
public class FacturaPagoServicio {

    private final FacturaPagoRepositorio facturaPagoRepositorio;

    public FacturaPagoServicio(FacturaPagoRepositorio facturaPagoRepositorio) {
        this.facturaPagoRepositorio = facturaPagoRepositorio;
    }

    public List<FacturaPago> listarTodos() {
        return facturaPagoRepositorio.findAll();
    }

    public Optional<FacturaPago> buscarPorId(Long id) {
        return facturaPagoRepositorio.findById(id);
    }

    public List<FacturaPago> listarPorFactura(Factura factura) {
        return facturaPagoRepositorio.findByFactura(factura);
    }

    public FacturaPago guardar(FacturaPago pago) {
        return facturaPagoRepositorio.save(pago);
    }

    public void eliminarPorId(Long id) {
        facturaPagoRepositorio.deleteById(id);
    }
}
