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
        if (usuarioRepositorio.findByUsername("andres").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("andres");
            admin.setPassword(passwordEncoder.encode("2005"));
            admin.setRol("ADMIN");
            admin.setActivo(true);
            usuarioRepositorio.save(admin);
            System.out.println(">> USUARIO 'andres' CREADO CON CLAVE '2005'");
        }

        // 2. Crear Empresa por defecto
        if (empresaRepositorio.count() == 0) {
            com.factura.facturacion.entidades.empresa.Empresa empresa = new com.factura.facturacion.entidades.empresa.Empresa();
            empresa.setRuc("1790012345001");
            empresa.setRazonSocial("MI EMPRESA S.A.");
            empresa.setNombreComercial("MI EMPRESA");
            empresa.setDireccionMatriz("Av. Amazonas y Naciones Unidas");
            empresa.setObligadoLlevarContabilidad("SI");
            empresa.setAmbiente("1"); // Pruebas
            empresa.setTipoEmision("1"); // Normal
            empresa = empresaRepositorio.save(empresa);
            System.out.println(">> EMPRESA 'MI EMPRESA' CREADA");

            // 3. Crear Establecimiento 001
            com.factura.facturacion.entidades.empresa.Establecimiento est = new com.factura.facturacion.entidades.empresa.Establecimiento();
            est.setEmpresa(empresa);
            est.setCodigo("001");
            est.setDireccion("Av. Amazonas y Naciones Unidas");
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
            System.out.println(">> PUNTO EMISION '001' CREADO");
        }

        // 5. Crear Usuario Vendedor (Check duplicados)
        try {
            if (usuarioRepositorio.findByUsername("vendedor").isEmpty()) {
                crearUsuario("vendedor", "12345", "VENDEDOR");
            }
        } catch (Exception e) {
            System.out.println(">> ALERTA: Usuario 'vendedor' ya existe o hay duplicados. Omitiendo creación.");
        }

        // 6. Crear Usuario Contador
        // 6. Crear Usuario Contador
        try {
            if (usuarioRepositorio.findByUsername("contador").isEmpty()) {
                crearUsuario("contador", "12345", "CONTADOR");
            }
        } catch (Exception e) {
            System.out.println(">> ALERTA: Usuario 'contador' ya existe o hay duplicados.");
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
