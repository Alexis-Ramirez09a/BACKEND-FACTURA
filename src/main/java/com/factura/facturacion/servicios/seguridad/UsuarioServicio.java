package com.factura.facturacion.servicios.seguridad;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.seguridad.Usuario;
import com.factura.facturacion.repositorios.seguridad.UsuarioRepositorio;

@Service
public class UsuarioServicio {

    private final UsuarioRepositorio repositorio;

    public UsuarioServicio(UsuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Usuario> listar() {
        return repositorio.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return repositorio.findById(id);
    }

    public java.util.Optional<Usuario> buscarPorUsername(String username) {
        return repositorio.findByUsername(username).stream().findFirst();
    }

    public Usuario guardar(Usuario usuario) {
        return repositorio.save(usuario);
    }

    public void eliminar(Long id) {
        repositorio.deleteById(id);
    }
}
