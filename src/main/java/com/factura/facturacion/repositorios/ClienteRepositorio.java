package com.factura.facturacion.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.factura.facturacion.entidades.catalogo.Cliente;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {

    // Buscar por identificación (RUC, cédula, pasaporte)
    Optional<Cliente> findByIdentificacion(String identificacion);

    // Buscar clientes por nombre que contenga un texto (búsqueda parcial)
    List<Cliente> findByNombreRazonSocialContainingIgnoreCase(String texto);

    // Buscar por nombre O identificación (Búsqueda General)
    List<Cliente> findByNombreRazonSocialContainingIgnoreCaseOrIdentificacionContainingIgnoreCase(String nombre,
            String identificacion);

    // Buscar por identificación parcial
    List<Cliente> findByIdentificacionContaining(String identificacion);

    List<Cliente> findByActivoTrue();
}
// Permite buscar un cliente por su identificación
// Permite buscar clientes cuyo nombre o razón social contenga un texto
// específico
// Maneja CRUD automático