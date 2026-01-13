package com.factura.facturacion.servicios.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.catalogo.Impuesto;
import com.factura.facturacion.entidades.catalogo.ImpuestoTarifa;
import com.factura.facturacion.repositorios.ImpuestoTarifaRepositorio;

@Service
public class ImpuestoTarifaServicio {

    private final ImpuestoTarifaRepositorio impuestoTarifaRepositorio;

    public ImpuestoTarifaServicio(ImpuestoTarifaRepositorio impuestoTarifaRepositorio) {
        this.impuestoTarifaRepositorio = impuestoTarifaRepositorio;
    }

    public List<ImpuestoTarifa> listarTodas() {
        return impuestoTarifaRepositorio.findAll();
    }

    public Optional<ImpuestoTarifa> buscarPorId(Long id) {
        return impuestoTarifaRepositorio.findById(id);
    }

    public List<ImpuestoTarifa> listarPorImpuesto(Impuesto impuesto) {
        return impuestoTarifaRepositorio.findByImpuesto(impuesto);
    }

    public Optional<ImpuestoTarifa> buscarPorImpuestoYCodigo(Impuesto impuesto, String codigoTarifa) {
        return impuestoTarifaRepositorio.findByCodigoTarifaAndImpuesto(codigoTarifa, impuesto);
    }

    public ImpuestoTarifa guardar(ImpuestoTarifa tarifa) {
        return impuestoTarifaRepositorio.save(tarifa);
    }

    public void eliminarPorId(Long id) {
        impuestoTarifaRepositorio.deleteById(id);
    }
}
