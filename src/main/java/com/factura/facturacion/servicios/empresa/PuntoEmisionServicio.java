package com.factura.facturacion.servicios.empresa;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.empresa.Establecimiento;
import com.factura.facturacion.entidades.empresa.PuntoEmision;
import com.factura.facturacion.repositorios.PuntoEmisionRepositorio;

@Service
public class PuntoEmisionServicio {

    private final PuntoEmisionRepositorio puntoEmisionRepositorio;

    public PuntoEmisionServicio(PuntoEmisionRepositorio puntoEmisionRepositorio) {
        this.puntoEmisionRepositorio = puntoEmisionRepositorio;
    }

    public List<PuntoEmision> listarTodos() {
        return puntoEmisionRepositorio.findAll();
    }

    public Optional<PuntoEmision> buscarPorId(Long id) {
        return puntoEmisionRepositorio.findById(id);
    }

    public List<PuntoEmision> listarPorEstablecimiento(Establecimiento establecimiento) {
        return puntoEmisionRepositorio.findByEstablecimiento(establecimiento);
    }

    public Optional<PuntoEmision> buscarPorEstablecimientoYCodigo(Establecimiento establecimiento, String codigo) {
        PuntoEmision punto = puntoEmisionRepositorio.findByEstablecimientoAndCodigo(establecimiento, codigo);
        return Optional.ofNullable(punto);
    }

    public PuntoEmision guardar(PuntoEmision puntoEmision) {
        return puntoEmisionRepositorio.save(puntoEmision);
    }

    public void eliminarPorId(Long id) {
        puntoEmisionRepositorio.deleteById(id);
    }
}
