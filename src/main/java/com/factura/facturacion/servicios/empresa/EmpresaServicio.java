package com.factura.facturacion.servicios.empresa;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.empresa.Empresa;
import com.factura.facturacion.repositorios.EmpresaRepositorio;

@Service
public class EmpresaServicio {

    private final EmpresaRepositorio empresaRepositorio;

    public EmpresaServicio(EmpresaRepositorio empresaRepositorio) {
        this.empresaRepositorio = empresaRepositorio;
    }

    public List<Empresa> listarTodas() {
        return empresaRepositorio.findAll();
    }

    public Optional<Empresa> buscarPorId(Long id) {
        return empresaRepositorio.findById(id);
    }

    public Optional<Empresa> buscarPorRuc(String ruc) {
        return empresaRepositorio.findByRuc(ruc) == null
                ? Optional.empty()
                : Optional.of(empresaRepositorio.findByRuc(ruc));
    }

    public Empresa guardar(Empresa empresa) {
        return empresaRepositorio.save(empresa);
    }

    public void eliminarPorId(Long id) {
        empresaRepositorio.deleteById(id);
    }
}

//organiza y expone todas las operaciones que se pueden hacer con Empresa, usando el repositorio, y luego esta clase será llamada desde los controladores REST.