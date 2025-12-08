package com.factura.facturacion.servicios.factura;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.factura.ComprobanteElectronico;
import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.repositorios.ComprobanteElectronicoRepositorio;

@Service
public class ComprobanteElectronicoServicio {

    private final ComprobanteElectronicoRepositorio comprobanteRepositorio;

    public ComprobanteElectronicoServicio(ComprobanteElectronicoRepositorio comprobanteRepositorio) {
        this.comprobanteRepositorio = comprobanteRepositorio;
    }

    public List<ComprobanteElectronico> listarTodos() {
        return comprobanteRepositorio.findAll();
    }

    public Optional<ComprobanteElectronico> buscarPorId(Long id) {
        return comprobanteRepositorio.findById(id);
    }

    public Optional<ComprobanteElectronico> buscarPorFactura(Factura factura) {
        return comprobanteRepositorio.findByFactura(factura);
    }

    public Optional<ComprobanteElectronico> buscarPorClaveAcceso(String claveAcceso) {
        return comprobanteRepositorio.findByClaveAcceso(claveAcceso);
    }

    public Optional<ComprobanteElectronico> buscarPorNumeroAutorizacion(String numeroAutorizacion) {
        return comprobanteRepositorio.findByNumeroAutorizacion(numeroAutorizacion);
    }

    public ComprobanteElectronico guardar(ComprobanteElectronico comprobante) {
        return comprobanteRepositorio.save(comprobante);
    }

    public void eliminarPorId(Long id) {
        comprobanteRepositorio.deleteById(id);
    }
}
//gestiona la lógica de negocio relacionada con los comprobantes electrónicos, como facturas, notas de crédito, etc. Proporciona métodos para buscar, guardar y eliminar comprobantes asociados a facturas específicas o mediante identificadores únicos como clave de acceso o número de autorización.