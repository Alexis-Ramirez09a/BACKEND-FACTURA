package com.factura.facturacion.repositorios.seguridad;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.factura.facturacion.entidades.seguridad.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    // Modificado a List temporalmente para manejar duplicados
    java.util.List<Usuario> findByUsername(String username);
}
