package com.factura.facturacion.servicios.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.catalogo.Impuesto;
import com.factura.facturacion.repositorios.ImpuestoRepositorio;

@Service
public class ImpuestoServicio {

    private final ImpuestoRepositorio impuestoRepositorio;

    public ImpuestoServicio(ImpuestoRepositorio impuestoRepositorio) {
        this.impuestoRepositorio = impuestoRepositorio;
    }

    public List<Impuesto> listarTodos() {
        return impuestoRepositorio.findAll();
    }

    public Optional<Impuesto> buscarPorId(Long id) {
        return impuestoRepositorio.findById(id);
    }

    public Optional<Impuesto> buscarPorCodigo(String codigo) {
        return impuestoRepositorio.findByCodigo(codigo);
    }

    public Impuesto guardar(Impuesto impuesto) {
        return impuestoRepositorio.save(impuesto);
    }

    public void eliminarPorId(Long id) {
        impuestoRepositorio.deleteById(id);
    }
}
