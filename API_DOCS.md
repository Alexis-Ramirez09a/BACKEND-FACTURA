# API Documentation - Facturación Electrónica SRI

## Resumen

Esta documentación detalla los endpoints disponibles para la integración del proceso de facturación electrónica con el SRI.

## Endpoints

### 1. Enviar Factura al SRI

Este endpoint inicia el proceso completo de facturación electrónica:

1. Generación de XML.
2. Firma Electrónica (Configurable: Mock para dev / Real para prod).
3. Envío al SRI (Recepción).
4. Solicitud de Autorización (si fue recibida).

**Definición:**

- **Método:** `POST`
- **URL:** `/api/sri/enviar/{facturaId}`

**Parámetros:**

- `facturaId` (Path Variable): ID en base de datos de la factura a procesar.

**Respuestas:**

#### Éxito (HTTP 200 OK)

Retorna el objeto `Factura` actualizado con el estado y los mensajes del SRI.

```json
{
  "id": 123,
  "secuencial": "001-001-000000123",
  "estado": "AUTORIZADO", // Estados: AUTORIZADO, RECHAZADA, DEVUELTA, ERROR_SISTEMA
  "fechaAutorizacion": "2025-01-12T15:30:00",
  "claveAcceso": "120120250117900...",
  "observacion": null // Contendrá mensajes de error si falla
  // ... otros campos de factura
}
```

#### Error - No Encontrado (HTTP 404)

Si el `facturaId` no existe.

---

## Flujo de Estados

El campo `estado` en la respuesta es vital para que el Frontend muestre feedback al usuario:

| Estado          | Significado                                                   | Acción Sugerida Frontend                                          |
| :-------------- | :------------------------------------------------------------ | :---------------------------------------------------------------- |
| `AUTORIZADO`    | Factura aceptada y autorizada por el SRI.                     | Mostrar check verde, habilitar descarga de RIDE/XML.              |
| `DEVUELTA`      | El SRI rechazó el formato o validaciones básicas (Recepción). | Mostrar error en rojo. Leer campo `observacion` para ver detalle. |
| `RECHAZADA`     | El SRI recibió pero no autorizó (reglas de negocio).          | Mostrar error. Leer campo `observacion`.                          |
| `ERROR_SISTEMA` | Fallo interno (conexión, firma, código).                      | Mostrar "Error interno, contacte soporte".                        |

## Notas para Desarrollo (Frontend)

- **Tiempos de respuesta:** El SRI puede demorar unos segundos. Mostrar un "spinner" o indicador de carga mientras se espera la respuesta del POST.
- **Pruebas sin Firma:** Actualmente el backend está configurado en estrategia `MOCK`. Simulará la firma pero intentará enviar al SRI (Ambiente Pruebas). Si fallan las credenciales SRI, verás estados de error, lo cual es esperado hasta tener credenciales válidas.
