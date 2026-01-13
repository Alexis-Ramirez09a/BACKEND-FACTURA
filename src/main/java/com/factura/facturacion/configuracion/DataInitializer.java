package com.factura.facturacion.configuracion;

import com.factura.facturacion.entidades.seguridad.Usuario;
import com.factura.facturacion.repositorios.seguridad.UsuarioRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final com.factura.facturacion.repositorios.EmpresaRepositorio empresaRepositorio;
    private final com.factura.facturacion.repositorios.EstablecimientoRepositorio establecimientoRepositorio;
    private final com.factura.facturacion.repositorios.PuntoEmisionRepositorio puntoEmisionRepositorio;
    private final com.factura.facturacion.repositorios.FormaPagoRepositorio formaPagoRepositorio;
    private final com.factura.facturacion.repositorios.ImpuestoRepositorio impuestoRepositorio;
    private final com.factura.facturacion.repositorios.ImpuestoTarifaRepositorio impuestoTarifaRepositorio;

    public DataInitializer(UsuarioRepositorio usuarioRepositorio,
            PasswordEncoder passwordEncoder,
            com.factura.facturacion.repositorios.EmpresaRepositorio empresaRepositorio,
            com.factura.facturacion.repositorios.EstablecimientoRepositorio establecimientoRepositorio,
            com.factura.facturacion.repositorios.PuntoEmisionRepositorio puntoEmisionRepositorio,
            com.factura.facturacion.repositorios.FormaPagoRepositorio formaPagoRepositorio,
            com.factura.facturacion.repositorios.ImpuestoRepositorio impuestoRepositorio,
            com.factura.facturacion.repositorios.ImpuestoTarifaRepositorio impuestoTarifaRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.empresaRepositorio = empresaRepositorio;
        this.establecimientoRepositorio = establecimientoRepositorio;
        this.puntoEmisionRepositorio = puntoEmisionRepositorio;
        this.formaPagoRepositorio = formaPagoRepositorio;
        this.impuestoRepositorio = impuestoRepositorio;
        this.impuestoTarifaRepositorio = impuestoTarifaRepositorio;
    }

    @Override
    public void run(String... args) throws Exception {
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
            // Actualizar
            com.factura.facturacion.entidades.empresa.Empresa empresa = empresaRepositorio.findAll().get(0);
            if (!"Tienda 24 de Mayo".equals(empresa.getRazonSocial())) {
                empresa.setRazonSocial("Tienda 24 de Mayo");
                empresa.setNombreComercial("Tienda 24 de Mayo");
                empresa.setDireccionMatriz("Via 24 de mayo");
                empresaRepositorio.save(empresa);
                System.out.println(">> EMPRESA ACTUALIZADA");
            }
        }

        // 5. Crear Formas de Pago
        crearFormaPago("01", "SIN UTILIZACION DEL SISTEMA FINANCIERO");
        crearFormaPago("19", "TARJETA DE CREDITO");
        crearFormaPago("20", "OTROS CON UTILIZACION DEL SISTEMA FINANCIERO");

        // 6. Crear Impuestos y Tarifas
        crearImpuestos();

        // 7. Crear Usuario Vendedor
        try {
            if (usuarioRepositorio.findByUsername("vendedor").isEmpty()) {
                crearUsuario("vendedor", "12345", "VENDEDOR");
            } else {
                Usuario vend = usuarioRepositorio.findByUsername("vendedor").get(0);
                if (!"VENDEDOR".equals(vend.getRol())) {
                    vend.setRol("VENDEDOR");
                    usuarioRepositorio.save(vend);
                }
            }
        } catch (Exception e) {
            System.out.println(">> ALERTA: Usuario 'vendedor' ya existe.");
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

    private void crearFormaPago(String codigo, String descripcion) {
        if (formaPagoRepositorio.findByCodigoSri(codigo).isEmpty()) {
            com.factura.facturacion.entidades.catalogo.FormaPago fp = new com.factura.facturacion.entidades.catalogo.FormaPago();
            fp.setCodigoSri(codigo);
            fp.setDescripcion(descripcion);
            fp.setActivo(true);
            formaPagoRepositorio.save(fp);
            System.out.println(">> FORMA PAGO " + codigo + " CREADA");
        }
    }

    private void crearImpuestos() {
        // Impuesto IVA
        com.factura.facturacion.entidades.catalogo.Impuesto iva = impuestoRepositorio.findByCodigo("2").orElse(null);
        if (iva == null) {
            iva = new com.factura.facturacion.entidades.catalogo.Impuesto();
            iva.setCodigo("2");
            iva.setNombre("IVA");
            iva.setDescripcion("Impuesto al Valor Agregado");
            iva = impuestoRepositorio.save(iva);
            System.out.println(">> IMPUESTO IVA CREADO");
        }

        // Tarifas IVA
        crearTarifa(iva, "0", "0%", new BigDecimal("0.00"));
        crearTarifa(iva, "2", "12%", new BigDecimal("12.00"));
        crearTarifa(iva, "6", "No Objeto de Impuesto", new BigDecimal("0.00"));
        crearTarifa(iva, "7", "Exento de IVA", new BigDecimal("0.00"));
    }

    private void crearTarifa(com.factura.facturacion.entidades.catalogo.Impuesto impuesto, String codigo, String desc,
            BigDecimal porcentaje) {
        if (impuestoTarifaRepositorio.findByCodigoTarifaAndImpuesto(codigo, impuesto).isEmpty()) {
            com.factura.facturacion.entidades.catalogo.ImpuestoTarifa tarifa = new com.factura.facturacion.entidades.catalogo.ImpuestoTarifa();
            tarifa.setImpuesto(impuesto);
            tarifa.setCodigoTarifa(codigo);
            tarifa.setDescripcion(desc);
            tarifa.setPorcentaje(porcentaje);
            tarifa.setActivo(true);
            impuestoTarifaRepositorio.save(tarifa);
            System.out.println(">> TARIFA IVA " + porcentaje + "% CREADA");
        }
    }
}
