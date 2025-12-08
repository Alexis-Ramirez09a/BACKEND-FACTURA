package com.factura.facturacion.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrincipalControlador {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // busca templates/login.html
    }

    @GetMapping("/facturas")
    public String facturas() {
        return "facturas"; // carga templates/facturas.html
    }

    @GetMapping("/nueva-factura")
    public String nuevaFactura() {
        return "nueva_factura"; // busca templates/nueva_factura.html
    }

    @GetMapping("/clientes/nuevo")
    public String nuevoCliente() {
        return "crear_cliente";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editarCliente() {
        return "editar_cliente";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProducto() {
        return "crear_producto";
    }

    @GetMapping("/clientes")
    public String clientes() {
        return "clientes";
    }

    @GetMapping("/productos")
    public String productos() {
        return "productos";
    }
}
