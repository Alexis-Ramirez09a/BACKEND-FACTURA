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

        // --- ENCABEZADO (EMPRESA) ---
        Paragraph title = new Paragraph("FACTURA ELECTRÓNICA", FONT_TITLE);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(new Paragraph(" ")); // Espacio

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[] { 1, 1 });

        // Columna Izquierda: Logo (simulado) y Datos Emisor
        PdfPCell cellEmisor = new PdfPCell();
        cellEmisor.setBorder(0);
        cellEmisor.addElement(new Paragraph(factura.getEmpresa().getRazonSocial(), FONT_HEADER));
        cellEmisor.addElement(new Paragraph("RUC: " + factura.getEmpresa().getRuc(), FONT_BODY));
        cellEmisor.addElement(new Paragraph("Dir: " + factura.getEmpresa().getDireccionMatriz(), FONT_BODY));
        cellEmisor.addElement(new Paragraph(
                "Obligado a llevar contabilidad: " + factura.getEmpresa().getObligadoLlevarContabilidad(), FONT_BODY));

        // Columna Derecha: Datos Factura
        PdfPCell cellFactura = new PdfPCell();
        cellFactura.setBorder(0);
        cellFactura.addElement(new Paragraph("No: " + factura.getSecuencial(), FONT_HEADER));
        cellFactura.addElement(new Paragraph(
                "Autorización: " + (factura.getClaveAcceso() != null ? factura.getClaveAcceso() : "PENDIENTE"),
                FONT_BODY));
        cellFactura.addElement(new Paragraph("Ambiente: " + factura.getEmpresa().getAmbiente(), FONT_BODY));
        cellFactura.addElement(new Paragraph("Emisión: " + factura.getEmpresa().getTipoEmision(), FONT_BODY));
        cellFactura.addElement(new Paragraph("Clave Acceso: " + factura.getClaveAcceso(), FONT_BODY));

        headerTable.addCell(cellEmisor);
        headerTable.addCell(cellFactura);
        doc.add(headerTable);

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(
                "----------------------------------------------------------------------------------------------------------------------------------"));
        doc.add(new Paragraph(" "));

        // --- DATOS CLIENTE ---
        PdfPTable clientTable = new PdfPTable(2);
        clientTable.setWidthPercentage(100);
        clientTable.setWidths(new float[] { 2, 6 }); // Labels vs Values

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        addClientRow(clientTable, "Razón Social:", factura.getCliente().getNombreRazonSocial());
        addClientRow(clientTable, "Identificación:", factura.getCliente().getIdentificacion());
        addClientRow(clientTable, "Fecha Emisión:",
                sdf.format(java.sql.Timestamp.valueOf(factura.getFechaEmision().atStartOfDay())));
        addClientRow(clientTable, "Dirección:", factura.getCliente().getDireccion());

        doc.add(clientTable);
        doc.add(new Paragraph(" "));

        // --- DETALLES ---
        PdfPTable itemsTable = new PdfPTable(5);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[] { 2, 4, 2, 2, 2 }); // Cod, Desc, Cant, Unit, Total

        // Headers
        addTableHeader(itemsTable, "Código");
        addTableHeader(itemsTable, "Descripción");
        addTableHeader(itemsTable, "Cant");
        addTableHeader(itemsTable, "P.Unit");
        addTableHeader(itemsTable, "Total");

        // Rows
        for (FacturaDetalle det : factura.getDetalles()) {
            itemsTable.addCell(new PdfPCell(new Phrase(det.getCodigoPrincipal(), FONT_BODY)));
            itemsTable.addCell(new PdfPCell(new Phrase(det.getDescripcion(), FONT_BODY)));
            itemsTable.addCell(new PdfPCell(new Phrase(det.getCantidad().toString(), FONT_BODY)));
            itemsTable.addCell(new PdfPCell(new Phrase(det.getPrecioUnitario().toString(), FONT_BODY)));
            itemsTable.addCell(new PdfPCell(new Phrase(det.getPrecioTotalSinImpuesto().toString(), FONT_BODY)));
        }
        doc.add(itemsTable);
        doc.add(new Paragraph(" "));

        // --- TOTALES ---
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(40);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.setWidths(new float[] { 1, 1 });

        addTotalRow(totalTable, "Subtotal 12%", factura.getSubtotalIva12().toPlainString());
        addTotalRow(totalTable, "Subtotal 0%", factura.getSubtotalIva0().toPlainString());
        addTotalRow(totalTable, "Subtotal No Obj", factura.getSubtotalNoObjetoIva().toPlainString());
        addTotalRow(totalTable, "Subtotal Exento", factura.getSubtotalExentoIva().toPlainString());
        addTotalRow(totalTable, "Subtotal Sin Imp", factura.getTotalSinImpuestos().toPlainString());
        addTotalRow(totalTable, "Descuento", factura.getTotalDescuento().toPlainString());
        addTotalRow(totalTable, "IVA 12%", factura.getValorIva().toPlainString());
        addTotalRow(totalTable, "Propina", factura.getPropina().toPlainString());
        addTotalRow(totalTable, "VALOR TOTAL", factura.getImporteTotal().toPlainString());

        doc.add(totalTable);

        doc.close();
        return out.toByteArray();
    }

    private void addClientRow(PdfPTable table, String label, String value) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, FONT_BODY_BOLD));
        cellLabel.setBorder(0);
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(value != null ? value : "", FONT_BODY));
        cellValue.setBorder(0);
        table.addCell(cellValue);
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        // header.setGrayFill(0.9f); // Opcional, si se desea fondo gris
        header.setBorderWidth(1);
        header.setPhrase(new Phrase(headerTitle, FONT_BODY_BOLD));
        table.addCell(header);
    }

    private void addTotalRow(PdfPTable table, String label, String value) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, FONT_BODY));
        cellLabel.setBorderWidth(1);
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(value, FONT_BODY));
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellValue.setBorderWidth(1);
        table.addCell(cellValue);
    }
}
