package com.factura.facturacion.servicios.factura;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.entidades.factura.LogEnvioSri;
import com.factura.facturacion.repositorios.LogEnvioSriRepositorio;

@Service
public class LogEnvioSriServicio {

    private final LogEnvioSriRepositorio logEnvioSriRepositorio;

    public LogEnvioSriServicio(LogEnvioSriRepositorio logEnvioSriRepositorio) {
        this.logEnvioSriRepositorio = logEnvioSriRepositorio;
    }

    public List<LogEnvioSri> listarTodos() {
        return logEnvioSriRepositorio.findAll();
    }

    public Optional<LogEnvioSri> buscarPorId(Long id) {
        return logEnvioSriRepositorio.findById(id);
    }

    public List<LogEnvioSri> listarPorFactura(Factura factura) {
        return logEnvioSriRepositorio.findByFactura(factura);
    }

    public List<LogEnvioSri> listarPorEstado(String estado) {
        return logEnvioSriRepositorio.findByEstado(estado);
    }

    public LogEnvioSri guardar(LogEnvioSri log) {
        return logEnvioSriRepositorio.save(log);
    }

    public void eliminarPorId(Long id) {
        logEnvioSriRepositorio.deleteById(id);
    }
}
//gestiona la lógica de negocio relacionada con los comprobantes electrónicos, como facturas, notas de crédito, etc. Proporciona métodos para buscar, guardar y eliminar comprobantes asociados a facturas específicas o mediante identificadores únicos como clave de acceso o número de autorización.