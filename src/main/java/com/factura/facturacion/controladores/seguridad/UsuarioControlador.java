package com.factura.facturacion.controladores.seguridad;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.factura.facturacion.dtos.seguridad.AuthRequest;
import com.factura.facturacion.dtos.seguridad.AuthResponse;
import com.factura.facturacion.entidades.seguridad.Usuario;
import com.factura.facturacion.seguridad.JwtTokenProvider;
import com.factura.facturacion.servicios.seguridad.UsuarioServicio;

@RestController
@RequestMapping("/api")
public class UsuarioControlador {

    private final UsuarioServicio servicio;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UsuarioControlador(UsuarioServicio servicio, AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.servicio = servicio;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userDetails);

        String rol = userDetails.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority().replace("ROLE_", ""))
                .orElse("USER");

        return ResponseEntity.ok(new AuthResponse(token, userDetails.getUsername(), rol));
    }

    @GetMapping("/usuarios")
    public List<Usuario> listar() {
        return servicio.listar();
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return servicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/usuarios")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Usuario crear(@RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return servicio.guardar(usuario);
    }

    @PutMapping("/usuarios/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id,
            @RequestBody Usuario usuario) {
        return servicio.buscarPorId(id)
                .map(u -> {
                    u.setUsername(usuario.getUsername());
                    if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                        u.setPassword(passwordEncoder.encode(usuario.getPassword()));
                    }
                    u.setRol(usuario.getRol());
                    u.setActivo(usuario.getActivo());
                    return ResponseEntity.ok(servicio.guardar(u));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/usuarios/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (servicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
