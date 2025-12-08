package com.factura.facturacion.servicios.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.catalogo.FormaPago;
import com.factura.facturacion.repositorios.FormaPagoRepositorio;

@Service
public class FormaPagoServicio {

    private final FormaPagoRepositorio formaPagoRepositorio;

    public FormaPagoServicio(FormaPagoRepositorio formaPagoRepositorio) {
        this.formaPagoRepositorio = formaPagoRepositorio;
    }

    public List<FormaPago> listarTodas() {
        return formaPagoRepositorio.findAll();
    }

    public Optional<FormaPago> buscarPorId(Long id) {
        return formaPagoRepositorio.findById(id);
    }

    public Optional<FormaPago> buscarPorCodigoSri(String codigoSri) {
        return formaPagoRepositorio.findByCodigoSri(codigoSri);
    }

    public List<FormaPago> listarActivas() {
        return formaPagoRepositorio.findByActivoTrue();
    }

    public FormaPago guardar(FormaPago formaPago) {
        return formaPagoRepositorio.save(formaPago);
    }

    public void eliminarPorId(Long id) {
        formaPagoRepositorio.deleteById(id);
    }
}
