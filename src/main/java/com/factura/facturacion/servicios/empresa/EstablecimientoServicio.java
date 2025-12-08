package com.factura.facturacion.servicios.empresa;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.empresa.Empresa;
import com.factura.facturacion.entidades.empresa.Establecimiento;
import com.factura.facturacion.repositorios.EstablecimientoRepositorio;

@Service
public class EstablecimientoServicio {

    private final EstablecimientoRepositorio establecimientoRepositorio;

    public EstablecimientoServicio(EstablecimientoRepositorio establecimientoRepositorio) {
        this.establecimientoRepositorio = establecimientoRepositorio;
    }

    public List<Establecimiento> listarTodos() {
        return establecimientoRepositorio.findAll();
    }

    public Optional<Establecimiento> buscarPorId(Long id) {
        return establecimientoRepositorio.findById(id);
    }

    public List<Establecimiento> listarPorEmpresa(Empresa empresa) {
        return establecimientoRepositorio.findByEmpresa(empresa);
    }

    public Optional<Establecimiento> buscarPorCodigo(String codigo) {
        Establecimiento est = establecimientoRepositorio.findByCodigo(codigo);
        return Optional.ofNullable(est);
    }

    public Establecimiento guardar(Establecimiento establecimiento) {
        return establecimientoRepositorio.save(establecimiento);
    }

    public void eliminarPorId(Long id) {
        establecimientoRepositorio.deleteById(id);
    }
}

