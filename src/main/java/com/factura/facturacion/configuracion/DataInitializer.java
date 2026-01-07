package com.factura.facturacion.configuracion;

import com.factura.facturacion.entidades.seguridad.Usuario;
import com.factura.facturacion.repositorios.seguridad.UsuarioRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final com.factura.facturacion.repositorios.EmpresaRepositorio empresaRepositorio;
    private final com.factura.facturacion.repositorios.EstablecimientoRepositorio establecimientoRepositorio;
    private final com.factura.facturacion.repositorios.PuntoEmisionRepositorio puntoEmisionRepositorio;

    public DataInitializer(UsuarioRepositorio usuarioRepositorio,
            PasswordEncoder passwordEncoder,
            com.factura.facturacion.repositorios.EmpresaRepositorio empresaRepositorio,
            com.factura.facturacion.repositorios.EstablecimientoRepositorio establecimientoRepositorio,
            com.factura.facturacion.repositorios.PuntoEmisionRepositorio puntoEmisionRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.empresaRepositorio = empresaRepositorio;
        this.establecimientoRepositorio = establecimientoRepositorio;
        this.puntoEmisionRepositorio = puntoEmisionRepositorio;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Crear Usuario Admin
        // 1. Crear Usuario Admin
        if (usuarioRepositorio.findByUsername("alexis").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("alexis");
            admin.setPassword(passwordEncoder.encode("0991"));
            admin.setRol("ADMINISTRADOR");
            admin.setActivo(true);
            usuarioRepositorio.save(admin);
            System.out.println(">> USUARIO 'alexis' CREADO CON CLAVE '0991'");
        } else {
            // Update role if exists (migration)
            Usuario admin = usuarioRepositorio.findByUsername("alexis").get(0);
            if (!"ADMINISTRADOR".equals(admin.getRol())) {
                admin.setRol("ADMINISTRADOR");
                usuarioRepositorio.save(admin);
                System.out.println(">> ROL DE 'alexis' ACTUALIZADO A 'ADMINISTRADOR'");
            }
        }

        // 2. Crear Empresa por defecto
        if (empresaRepositorio.count() == 0) {
            com.factura.facturacion.entidades.empresa.Empresa empresa = new com.factura.facturacion.entidades.empresa.Empresa();
            empresa.setRuc("1790012345678"); // RUC dummy preferible de 13 digitos
            empresa.setRazonSocial("Tienda 24 de Mayo");
            empresa.setNombreComercial("Tienda 24 de Mayo");
            empresa.setDireccionMatriz("Via 24 de mayo");
            empresa.setObligadoLlevarContabilidad("SI");
            empresa.setAmbiente("1"); // Pruebas
            empresa.setTipoEmision("1"); // Normal
            empresa = empresaRepositorio.save(empresa);
            System.out.println(">> EMPRESA 'Tienda 24 de Mayo' CREADA");

            // 3. Crear Establecimiento 001
            com.factura.facturacion.entidades.empresa.Establecimiento est = new com.factura.facturacion.entidades.empresa.Establecimiento();
            est.setEmpresa(empresa);
            est.setCodigo("001");
            est.setDireccion("Via 24 de mayo (Matriz)");
            est.setDescripcion("Matriz");
            est = establecimientoRepositorio.save(est);
            System.out.println(">> ESTABLECIMIENTO '001' CREADO");

            // 4. Crear Punto Emision 001
            com.factura.facturacion.entidades.empresa.PuntoEmision pto = new com.factura.facturacion.entidades.empresa.PuntoEmision();
            pto.setEstablecimiento(est);
            pto.setCodigo("001");
            pto.setDescripcion("Caja Principal");
            pto.setActivo(true);
            puntoEmisionRepositorio.save(pto);
            System.out.println(">> PUNTO EMISION '001' CREADO");
        } else {
            // Actualizar datos de empresa existente (para que el usuario vea el cambio sin
            // borrar BD)
            com.factura.facturacion.entidades.empresa.Empresa empresa = empresaRepositorio.findAll().get(0);
            if (!"Tienda 24 de Mayo".equals(empresa.getRazonSocial())) {
                empresa.setRazonSocial("Tienda 24 de Mayo");
                empresa.setNombreComercial("Tienda 24 de Mayo");
                empresa.setDireccionMatriz("Via 24 de mayo");
                empresaRepositorio.save(empresa);
                System.out.println(">> EMPRESA ACTUALIZADA A 'Tienda 24 de Mayo'");
            }
        }

        // 5. Crear Usuario Vendedor (Check duplicados)
        try {
            if (usuarioRepositorio.findByUsername("vendedor").isEmpty()) {
                crearUsuario("vendedor", "12345", "VENDEDOR");
            } else {
                Usuario vend = usuarioRepositorio.findByUsername("vendedor").get(0);
                if (!"VENDEDOR".equals(vend.getRol())) {
                    vend.setRol("VENDEDOR");
                    usuarioRepositorio.save(vend);
                    System.out.println(">> ROL DE 'vendedor' ACTUALIZADO A 'VENDEDOR'");
                }
            }
        } catch (Exception e) {
            System.out.println(">> ALERTA: Usuario 'vendedor' ya existe o hay duplicados. Omitiendo creación.");
        }
    }

    private void crearUsuario(String username, String password, String rol) {
        Usuario user = new Usuario();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRol(rol);
        user.setActivo(true);
        usuarioRepositorio.save(user);
        System.out.println(">> USUARIO '" + username + "' CREADO");
    }
}
