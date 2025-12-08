package com.factura.facturacion.dtos.seguridad;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
