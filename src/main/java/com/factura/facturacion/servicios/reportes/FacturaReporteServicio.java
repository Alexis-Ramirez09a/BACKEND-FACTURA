package com.factura.facturacion.servicios.reportes;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

import org.springframework.stereotype.Service;

import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.entidades.factura.FacturaDetalle;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class FacturaReporteServicio {

    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    private static final Font FONT_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font FONT_BODY = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font FONT_BODY_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

    public byte[] generarPdf(Factura factura) throws Exception {
        Document doc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);

        doc.open();

        // --- COLORES & FUENTES ---
        com.lowagie.text.pdf.BaseFont bf = com.lowagie.text.pdf.BaseFont.createFont(
                com.lowagie.text.pdf.BaseFont.HELVETICA, com.lowagie.text.pdf.BaseFont.CP1252,
                com.lowagie.text.pdf.BaseFont.NOT_EMBEDDED);
        Font fontTitle = new Font(bf, 18, Font.BOLD, java.awt.Color.DARK_GRAY);
        Font fontSubtitle = new Font(bf, 12, Font.BOLD, java.awt.Color.BLACK);
        Font fontRegular = new Font(bf, 10, Font.NORMAL, java.awt.Color.BLACK);
        Font fontSmall = new Font(bf, 8, Font.NORMAL, java.awt.Color.GRAY);
        Font fontTableHeader = new Font(bf, 10, Font.BOLD, java.awt.Color.WHITE);

        java.awt.Color headerBgColor = new java.awt.Color(50, 60, 160); // Azul profesional
        java.awt.Color lightGray = new java.awt.Color(240, 240, 240);

        // --- TABLA MAESTRA (2 columnas: Izq Empresa, Der Factura Info) ---
        PdfPTable mainTable = new PdfPTable(2);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new float[] { 1.2f, 1f });
        mainTable.getDefaultCell().setBorder(0);

        // --- COLUMNA 1: INFO EMPRESA ---
        PdfPCell cellEmpresa = new PdfPCell();
        cellEmpresa.setBorder(0);

        // Nombre Empresa (Grande) se toma de la BD (DataInitializer setea Tienda 24 de
        // Mayo)
        Paragraph pEmpresa = new Paragraph(factura.getEmpresa().getRazonSocial(), fontTitle);
        cellEmpresa.addElement(pEmpresa);

        // Dirección y Detalles
        String dir = factura.getEmpresa().getDireccionMatriz();
        // Dirección hardcoded extra si falta en BD o para asegurar "Via 24 de mayo"
        if (dir == null || dir.isEmpty())
            dir = "Via 24 de mayo";

        cellEmpresa.addElement(new Paragraph(dir, fontRegular));
        cellEmpresa.addElement(new Paragraph("RUC: " + factura.getEmpresa().getRuc(), fontRegular));
        // FORZADO A 'SI' POR SOLICITUD DE USUARIO
        cellEmpresa.addElement(new Paragraph(
                "Obligado a llevar contabilidad: SI", fontSmall));
        cellEmpresa.addElement(new Paragraph("\n")); // Espacio

        mainTable.addCell(cellEmpresa);

        // --- COLUMNA 2: INFO FACTURA (Recuadro) ---
        PdfPCell cellFactura = new PdfPCell();
        cellFactura.setBorder(0);

        // Tabla anidada para el borde
        PdfPTable infoTable = new PdfPTable(1);
        infoTable.setWidthPercentage(100);

        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorderWidth(1);
        infoCell.setBorderColor(java.awt.Color.LIGHT_GRAY);
        infoCell.setPadding(10);

        // Contenido del recuadro
        infoCell.addElement(new Paragraph("R.U.C.: " + factura.getEmpresa().getRuc(), fontSubtitle));
        infoCell.addElement(new Paragraph("FACTURA", fontTitle));
        infoCell.addElement(new Paragraph("No. " + factura.getSecuencial(), fontSubtitle));
        infoCell.addElement(new Paragraph("Fecha: " + factura.getFechaEmision(), fontRegular));
        infoCell.addElement(new Paragraph("AUTORIZACIÓN:", fontSmall));
        infoCell.addElement(new Paragraph(factura.getClaveAcceso(), fontSmall));

        infoTable.addCell(infoCell);
        cellFactura.addElement(infoTable);

        mainTable.addCell(cellFactura);

        doc.add(mainTable);
        doc.add(new Paragraph("\n"));

        // --- INFO CLIENTE (Banda gris) ---
        PdfPTable clientTable = new PdfPTable(1);
        clientTable.setWidthPercentage(100);

        PdfPCell clientCell = new PdfPCell();
        clientCell.setBackgroundColor(lightGray);
        clientCell.setPadding(8);
        clientCell.setBorder(0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaStr = sdf.format(java.sql.Timestamp.valueOf(factura.getFechaEmision().atStartOfDay()));

        Paragraph pCliente = new Paragraph();
        pCliente.add(new Phrase("Cliente: ", fontSubtitle));
        pCliente.add(new Phrase(factura.getCliente().getNombreRazonSocial() + "   ", fontRegular));
        pCliente.add(new Phrase("RUC/CI: ", fontSubtitle));
        pCliente.add(new Phrase(factura.getCliente().getIdentificacion() + "   ", fontRegular));
        pCliente.add(new Phrase("Fecha Emisión: ", fontSubtitle));
        pCliente.add(new Phrase(fechaStr, fontRegular));

        clientCell.addElement(pCliente);

        Paragraph pDirCli = new Paragraph();
        pDirCli.add(new Phrase("Dirección: ", fontSubtitle));
        pDirCli.add(new Phrase(factura.getCliente().getDireccion() != null ? factura.getCliente().getDireccion() : "-",
                fontRegular));

        clientCell.addElement(pDirCli);

        clientTable.addCell(clientCell);
        doc.add(clientTable);
        doc.add(new Paragraph("\n"));

        // --- DETALLES DE PRODUCTOS ---
        PdfPTable itemsTable = new PdfPTable(5);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[] { 1.5f, 4f, 1.5f, 1.5f, 1.5f }); // Cod, Desc, Cant, Unit, Total

        // Headers con fondo azul
        addStyledHeader(itemsTable, "Cód.", fontTableHeader, headerBgColor);
        addStyledHeader(itemsTable, "Descripción", fontTableHeader, headerBgColor);
        addStyledHeader(itemsTable, "Cant.", fontTableHeader, headerBgColor);
        addStyledHeader(itemsTable, "P.Unit", fontTableHeader, headerBgColor);
        addStyledHeader(itemsTable, "Total", fontTableHeader, headerBgColor);

        // Rows
        for (com.factura.facturacion.entidades.factura.FacturaDetalle det : factura.getDetalles()) {
            addStyledCell(itemsTable, det.getCodigoPrincipal(), fontRegular);
            addStyledCell(itemsTable, det.getDescripcion(), fontRegular);
            addStyledCell(itemsTable, det.getCantidad().toString(), fontRegular);
            addStyledCell(itemsTable, det.getPrecioUnitario().toString(), fontRegular);
            addStyledCell(itemsTable, det.getPrecioTotalSinImpuesto().toString(), fontRegular);
        }

        // Rellenar filas vacías para estética (opcional: o solo dejar espacio)
        doc.add(itemsTable);
        doc.add(new Paragraph("\n"));

        // --- TOTALES ---
        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setWidthPercentage(100);
        footerTable.setWidths(new float[] { 6f, 4f });

        // Celda Izq: Info Adicional o Pagos (Vacio por ahora)
        PdfPCell cellLeft = new PdfPCell(new Paragraph(
                "Información Adicional\nEmail: soporte@tienda24mayo.com\nTelf: 0991234567\nDirección: Via 24 de Mayo",
                fontSmall));
        cellLeft.setBorderWidth(1);
        cellLeft.setBorderColor(java.awt.Color.LIGHT_GRAY);
        cellLeft.setPadding(5);
        footerTable.addCell(cellLeft);

        // Celda Der: Totales
        PdfPCell cellTotales = new PdfPCell();
        cellTotales.setBorder(0);

        PdfPTable tTable = new PdfPTable(2);
        tTable.setWidthPercentage(100);

        addTotalRow(tTable, "Subtotal 12%", factura.getSubtotalIva12().toPlainString(), fontRegular, fontSubtitle);
        addTotalRow(tTable, "Subtotal 0%", factura.getSubtotalIva0().toPlainString(), fontRegular, fontSubtitle);
        addTotalRow(tTable, "IVA 12%", factura.getValorIva().toPlainString(), fontRegular, fontSubtitle);
        addTotalRow(tTable, "TOTAL", factura.getImporteTotal().toPlainString(), fontSubtitle, fontTitle); // Total más
                                                                                                          // grande

        cellTotales.addElement(tTable);
        footerTable.addCell(cellTotales);

        doc.add(footerTable);

        doc.close();
        return out.toByteArray();
    }

    private void addStyledHeader(PdfPTable table, String text, Font font, java.awt.Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addStyledCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        // cell.setBorderWidthBottom(1);
        // cell.setBorderColorBottom(java.awt.Color.LIGHT_GRAY);
        table.addCell(cell);
    }

    private void addClientRow(PdfPTable table, String label, String value) {
        // Depreciado por el nuevo diseño
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        // Depreciado
    }

    private void addTotalRow(PdfPTable table, String label, String value) {
        // Depreciado
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font fontLbl, Font fontVal) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, fontLbl));
        cellLabel.setBorderWidth(1);
        cellLabel.setBorderColor(java.awt.Color.LIGHT_GRAY);
        cellLabel.setPadding(4);
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(value, fontVal));
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellValue.setBorderWidth(1);
        cellValue.setBorderColor(java.awt.Color.LIGHT_GRAY);
        cellValue.setPadding(4);
        table.addCell(cellValue);
    }

}
