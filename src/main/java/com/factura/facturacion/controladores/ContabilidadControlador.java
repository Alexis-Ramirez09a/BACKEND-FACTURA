package com.factura.facturacion.controladores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.factura.facturacion.repositorios.FacturaRepositorio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/contabilidad")
@Tag(name = "Contabilidad", description = "Endpoints para reportes y totales")
public class ContabilidadControlador {

    @Autowired
    private FacturaRepositorio facturaRepositorio;

    @GetMapping("/total-rango")
    @Operation(summary = "Obtener total de ventas en un rango de fechas")
    public ResponseEntity<BigDecimal> obtenerTotalRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        BigDecimal total = facturaRepositorio.sumTotalByFechaEmisionBetweenAndEstadoNot(desde, hasta);
        return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
    }

    @GetMapping("/total-dia")
    @Operation(summary = "Obtener total de ventas de un día específico (default: hoy)")
    public ResponseEntity<BigDecimal> obtenerTotalDia(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate targetDate = (fecha != null) ? fecha : LocalDate.now();
        BigDecimal total = facturaRepositorio.sumTotalByFechaEmisionBetweenAndEstadoNot(targetDate, targetDate);
        return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
    }

    @GetMapping("/total-mes")
    @Operation(summary = "Obtener total de ventas de un mes específico")
    public ResponseEntity<BigDecimal> obtenerTotalMes(
            @RequestParam int anio,
            @RequestParam int mes) {

        YearMonth yearMonth = YearMonth.of(anio, mes);
        LocalDate inicio = yearMonth.atDay(1);
        LocalDate fin = yearMonth.atEndOfMonth();

        BigDecimal total = facturaRepositorio.sumTotalByFechaEmisionBetweenAndEstadoNot(inicio, fin);
        return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
    }
}
