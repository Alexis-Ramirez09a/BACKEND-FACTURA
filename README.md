# Facturación Electrónica - Servicio Backend

Este proyecto provee una API REST para la gestión de facturación electrónica, incluyendo clientes, productos, generación de reportes y simulación de envío al SRI.

## Ejecución

1. **Requisitos**: Java 17, Maven.
2. **Base de Datos**: PostgreSQL (configurar credenciales en `application.properties` si es diferente a lo default).
3. **Iniciar Aplicación**:
   ```bash
   ./mvnw spring-boot:run
   ```
4. **Acceso**: La aplicación corre en `http://localhost:8080`.

## Documentación API (Swagger/OpenAPI)
Una vez iniciada la aplicación, visitar:
- [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Seguridad y Roles

Se han creado los siguientes usuarios por defecto:
- **Admin**: `andres` / `2005` (Acceso total)
- **Vendedor**: `vendedor` / `12345` (Limitado)
- **Contador**: `contador` / `12345` (Limitado)

Para acceder a los endpoints protegidos, primero ejecutar `/api/auth/login` para obtener el Token JWT.

## Gestión de Usuarios
Endpoint: `/api/usuarios`
- **Crear/Editar/Eliminar**: Solo rol **ADMIN**.

## Reportes
Formatos disponibles: PDF.
URL: `/api/reportes/`
- `/api/reportes/clientes`
- `/api/reportes/productos`
- `/api/reportes/facturas`

## Envío al SRI (Simulación)
Endpoint para simular el envío de una factura:
- `POST /api/sri/enviar/{id_factura}`
- Simula tiempo de espera y responde aleatoriamente con Estado `AUTORIZADA` o `RECHAZADA`.
- Genera fecha de autorización.
