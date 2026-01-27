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
    private final com.factura.facturacion.repositorios.SecuencialDocumentoRepositorio secuencialDocumentoRepositorio;

    public DataInitializer(UsuarioRepositorio usuarioRepositorio,
            PasswordEncoder passwordEncoder,
            com.factura.facturacion.repositorios.EmpresaRepositorio empresaRepositorio,
            com.factura.facturacion.repositorios.EstablecimientoRepositorio establecimientoRepositorio,
            com.factura.facturacion.repositorios.PuntoEmisionRepositorio puntoEmisionRepositorio,
            com.factura.facturacion.repositorios.FormaPagoRepositorio formaPagoRepositorio,
            com.factura.facturacion.repositorios.ImpuestoRepositorio impuestoRepositorio,
            com.factura.facturacion.repositorios.ImpuestoTarifaRepositorio impuestoTarifaRepositorio,
            com.factura.facturacion.repositorios.SecuencialDocumentoRepositorio secuencialDocumentoRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.empresaRepositorio = empresaRepositorio;
        this.establecimientoRepositorio = establecimientoRepositorio;
        this.puntoEmisionRepositorio = puntoEmisionRepositorio;
        this.formaPagoRepositorio = formaPagoRepositorio;
        this.impuestoRepositorio = impuestoRepositorio;
        this.impuestoTarifaRepositorio = impuestoTarifaRepositorio;
        this.secuencialDocumentoRepositorio = secuencialDocumentoRepositorio;
    }

    @Override
    public void run(String... args) throws Exception {
        // ... (Previous logic for Admin, Empresa, etc.) ...

        // // 1. Crear Usuario Admin
        // if (usuarioRepositorio.findByUsername("alexis").isEmpty()) {
        // Usuario admin = new Usuario();
        // admin.setUsername("alexis");
        // admin.setPassword(passwordEncoder.encode("0991"));
        // admin.setRol("ADMINISTRADOR");
        // admin.setActivo(true);
        // usuarioRepositorio.save(admin);
        // System.out.println(">> USUARIO 'alexis' CREADO CON CLAVE '0991'");
        // } else {
        // Usuario admin = usuarioRepositorio.findByUsername("alexis").get(0);
        // if (!"ADMINISTRADOR".equals(admin.getRol())) {
        // admin.setRol("ADMINISTRADOR");
        // usuarioRepositorio.save(admin);
        // System.out.println(">> ROL DE 'alexis' ACTUALIZADO A 'ADMINISTRADOR'");
        // }
        // }

        // 2. Crear Empresa por defecto
        com.factura.facturacion.entidades.empresa.Empresa empresa = null;
        if (empresaRepositorio.count() == 0) {
            empresa = new com.factura.facturacion.entidades.empresa.Empresa();
            empresa.setRuc("1104106453001"); // RUC REAL DEL USUARIO
            empresa.setRazonSocial("CARLOS ANDRES SOTOMAYOR FIGUEROA");
            empresa.setNombreComercial("CARLOS SOTOMAYOR");
            empresa.setDireccionMatriz("Loja / Loja / 24 de Mayo");
            empresa.setObligadoLlevarContabilidad("NO");
            empresa.setAmbiente("1"); // Pruebas
            empresa.setTipoEmision("1"); // Normal
            empresa = empresaRepositorio.save(empresa);
            System.out.println(">> EMPRESA 'CARLOS SOTOMAYOR' CREADA");
        } else {
            empresa = empresaRepositorio.findAll().get(0);
            // Actualizar RUC y datos si son diferentes (para corregir el dummy)
            if (!"1104106453001".equals(empresa.getRuc())) {
                empresa.setRuc("1104106453001");
                empresa.setRazonSocial("CARLOS ANDRES SOTOMAYOR FIGUEROA");
                empresa.setNombreComercial("CARLOS SOTOMAYOR");
                empresa.setDireccionMatriz("Loja / Loja / 24 de Mayo");
                empresa.setObligadoLlevarContabilidad("NO");
                empresaRepositorio.save(empresa);
                System.out.println(">> EMPRESA ACTUALIZADA A DATOS REALES");
            }
        }

        // 3. Crear Establecimiento 001
        com.factura.facturacion.entidades.empresa.Establecimiento est;
        if (establecimientoRepositorio.findByCodigoAndEmpresa("001", empresa).isEmpty()) {
            est = new com.factura.facturacion.entidades.empresa.Establecimiento();
            est.setEmpresa(empresa);
            est.setCodigo("001");
            est.setDireccion("Via 24 de mayo (Matriz)");
            est.setDescripcion("Matriz");
            est = establecimientoRepositorio.save(est);
            System.out.println(">> ESTABLECIMIENTO '001' CREADO");
        } else {
            est = establecimientoRepositorio.findByCodigoAndEmpresa("001", empresa).get();
        }

        // 4. Crear Punto Emision 001
        com.factura.facturacion.entidades.empresa.PuntoEmision pto;
        if (puntoEmisionRepositorio.findByCodigoAndEstablecimiento("001", est).isEmpty()) {
            pto = new com.factura.facturacion.entidades.empresa.PuntoEmision();
            pto.setEstablecimiento(est);
            pto.setCodigo("001");
            pto.setDescripcion("Caja Principal");
            pto.setActivo(true);
            pto = puntoEmisionRepositorio.save(pto);
            System.out.println(">> PUNTO EMISION '001' CREADO");
        } else {
            pto = puntoEmisionRepositorio.findByCodigoAndEstablecimiento("001", est).get();
        }

        // 5. Crear Formas de Pago
        crearFormaPago("01", "SIN UTILIZACION DEL SISTEMA FINANCIERO");
        crearFormaPago("19", "TARJETA DE CREDITO");
        crearFormaPago("20", "OTROS CON UTILIZACION DEL SISTEMA FINANCIERO");

        // 6. Crear Impuestos y Tarifas
        crearImpuestos();

        // 7. Configurar Secuencial Facturas
        configurarSecuencial(est, pto, "01"); // 01 = Factura

        // 8. Crear Usuario Vendedor
        // try {
        // if (usuarioRepositorio.findByUsername("vendedor").isEmpty()) {
        // crearUsuario("vendedor", "12345", "VENDEDOR");
        // } else {
        // Usuario vend = usuarioRepositorio.findByUsername("vendedor").get(0);
        // if (!"VENDEDOR".equals(vend.getRol())) {
        // vend.setRol("VENDEDOR");
        // usuarioRepositorio.save(vend);
        // }
        // }
        // } catch (Exception e) {
        // System.out.println(">> ALERTA: Usuario 'vendedor' ya existe.");
        // }
    }

    private void configurarSecuencial(com.factura.facturacion.entidades.empresa.Establecimiento est,
            com.factura.facturacion.entidades.empresa.PuntoEmision pto,
            String tipoComprobante) {
        if (secuencialDocumentoRepositorio
                .findByTipoComprobanteAndEstablecimientoAndPuntoEmision(tipoComprobante, est, pto).isEmpty()) {
            com.factura.facturacion.entidades.factura.SecuencialDocumento s = new com.factura.facturacion.entidades.factura.SecuencialDocumento();
            s.setEstablecimiento(est);
            s.setPuntoEmision(pto);
            s.setTipoComprobante(tipoComprobante);
            s.setUltimoSecuencial(0); // Uses Integer, starts at 0
            secuencialDocumentoRepositorio.save(s);
            System.out.println(">> SECUENCIAL GENERADO para " + tipoComprobante + " (Est: 001, Pto: 001)");
        }
    }

    // ... (rest of helper methods: crearUsuario, crearFormaPago, crearImpuestos,
    // crearTarifa) ...

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
