package com.factura.facturacion.seguridad;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // Clave secreta LARGA (mínimo 32 caracteres)
    private static final String SECRET = "mi-clave-super-secreta-1234567890-abcdef";
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 1 día

    // Creamos la llave a partir del texto, SIN Base64
    private static final Key KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public static String generarToken(String username) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(KEY) // sin Decoders.BASE64
                .compact();
    }
}
