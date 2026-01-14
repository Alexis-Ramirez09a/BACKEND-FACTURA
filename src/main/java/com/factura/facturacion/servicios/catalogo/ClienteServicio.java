package com.factura.facturacion.servicios.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.catalogo.Cliente;
import com.factura.facturacion.repositorios.ClienteRepositorio;

@Service
public class ClienteServicio {

    private final ClienteRepositorio clienteRepositorio;

    public ClienteServicio(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    public List<Cliente> listarTodos() {
        return clienteRepositorio.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepositorio.findById(id);
    }

    public Optional<Cliente> buscarPorIdentificacion(String identificacion) {
        return clienteRepositorio.findByIdentificacion(identificacion);
    }

    public List<Cliente> buscarPorNombre(String texto) {
        // Ahora busca por nombre O identificación
        return clienteRepositorio.findByNombreRazonSocialContainingIgnoreCaseOrIdentificacionContainingIgnoreCase(texto,
                texto);
    }

    public List<Cliente> buscarPorIdentificacionParcial(String texto) {
        return clienteRepositorio.findByIdentificacionContaining(texto);
    }

    public List<Cliente> listarActivos() {
        return clienteRepositorio.findByActivoTrue();
    }

    public Cliente guardar(Cliente cliente) {
        return clienteRepositorio.save(cliente);
    }

    public void eliminarPorId(Long id) {
        // Hard delete requested by user
        clienteRepositorio.deleteById(id);
    }
}
