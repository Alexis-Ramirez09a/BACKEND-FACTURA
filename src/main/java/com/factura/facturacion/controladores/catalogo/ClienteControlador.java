package com.factura.facturacion.controladores.catalogo;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.factura.facturacion.entidades.catalogo.Cliente;
import com.factura.facturacion.servicios.catalogo.ClienteServicio;

@RestController
@RequestMapping("/api/clientes")
public class ClienteControlador {

    private final ClienteServicio clienteServicio;

    public ClienteControlador(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
    }

    // Listar todos los clientes
    @GetMapping
    public List<Cliente> listar() {
        return clienteServicio.listarActivos();
    }

    // Buscar cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return clienteServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar cliente por identificación (RUC/Cédula/Pasaporte)
    @GetMapping("/identificacion/{identificacion}")
    public ResponseEntity<Cliente> buscarPorIdentificacion(@PathVariable String identificacion) {
        return clienteServicio.buscarPorIdentificacion(identificacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar clientes por nombre (búsqueda parcial)
    @GetMapping("/buscar")
    public List<Cliente> buscarPorNombre(@RequestParam("q") String texto) {
        return clienteServicio.buscarPorNombre(texto);
    }

    // Buscar clientes por identificación (búsqueda parcial)
    @GetMapping("/buscar-identificacion")
    public List<Cliente> buscarPorIdentificacionParcial(@RequestParam("q") String texto) {
        return clienteServicio.buscarPorIdentificacionParcial(texto);
    }

    // Crear nuevo cliente
    @PostMapping
    public Cliente crear(@jakarta.validation.Valid @RequestBody Cliente cliente) {
        return clienteServicio.guardar(cliente);
    }

    // Actualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id,
            @jakarta.validation.Valid @RequestBody Cliente cliente) {
        return clienteServicio.buscarPorId(id)
                .map(existente -> {
                    cliente.setId(id);
                    Cliente actualizado = clienteServicio.guardar(cliente);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar cliente
    @DeleteMapping("/{id}")
    // @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (clienteServicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        clienteServicio.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
