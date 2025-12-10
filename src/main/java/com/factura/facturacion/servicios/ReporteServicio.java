package com.factura.facturacion.servicios;

import com.factura.facturacion.entidades.catalogo.Cliente;
import com.factura.facturacion.entidades.catalogo.Producto;
import com.factura.facturacion.entidades.factura.Factura;
import com.factura.facturacion.repositorios.ClienteRepositorio;
import com.factura.facturacion.repositorios.ProductoRepositorio;
import com.factura.facturacion.repositorios.FacturaRepositorio;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReporteServicio {

    private final ClienteRepositorio clienteRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final FacturaRepositorio facturaRepositorio;
    private final com.factura.facturacion.repositorios.EmpresaRepositorio empresaRepositorio;

    public ReporteServicio(ClienteRepositorio clienteRepositorio, ProductoRepositorio productoRepositorio,
            FacturaRepositorio facturaRepositorio,
            com.factura.facturacion.repositorios.EmpresaRepositorio empresaRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.facturaRepositorio = facturaRepositorio;
        this.empresaRepositorio = empresaRepositorio;
    }

    public byte[] generarReporteClientes() throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        addCompanyHeader(document, "Reporte de Clientes");

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.addCell(getHeaderCell("Identificación"));
        table.addCell(getHeaderCell("Nombre"));
        table.addCell(getHeaderCell("Email"));
        table.addCell(getHeaderCell("Teléfono"));

        List<Cliente> clientes = clienteRepositorio.findAll();
        for (Cliente cliente : clientes) {
            table.addCell(cliente.getIdentificacion());
            table.addCell(cliente.getNombreRazonSocial());
            table.addCell(cliente.getCorreo() != null ? cliente.getCorreo() : "");
            table.addCell(cliente.getTelefono() != null ? cliente.getTelefono() : "");
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    public byte[] generarReporteProductos() throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        addCompanyHeader(document, "Reporte de Productos");

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getHeaderCell("Código"));
        table.addCell(getHeaderCell("Descripción"));
        table.addCell(getHeaderCell("Precio Unitario"));

        List<Producto> productos = productoRepositorio.findAll();
        for (Producto producto : productos) {
            table.addCell(producto.getCodigoPrincipal());
            table.addCell(producto.getDescripcion());
            table.addCell(producto.getPrecioUnitario().toString());
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    public byte[] generarReporteFacturas() throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        addCompanyHeader(document, "Reporte de Facturas");

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.addCell(getHeaderCell("Secuencial"));
        table.addCell(getHeaderCell("Fecha"));
        table.addCell(getHeaderCell("Cliente"));
        table.addCell(getHeaderCell("Total"));

        List<Factura> facturas = facturaRepositorio.findAll();
        java.math.BigDecimal totalGeneral = java.math.BigDecimal.ZERO;

        for (Factura factura : facturas) {
            table.addCell(factura.getSecuencial());
            table.addCell(factura.getFechaEmision().toString());
            table.addCell(factura.getRazonSocialComprador());
            table.addCell(factura.getImporteTotal().toString());
            totalGeneral = totalGeneral.add(factura.getImporteTotal());
        }

        // Add Total Row
        PdfPCell cellTotalLabel = new PdfPCell(
                new Phrase("TOTAL GENERAL", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cellTotalLabel.setColspan(3);
        cellTotalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellTotalLabel);

        PdfPCell cellTotalValue = new PdfPCell(
                new Phrase(totalGeneral.toString(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        table.addCell(cellTotalValue);

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    private PdfPCell getHeaderCell(String text) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private void addCompanyHeader(Document document, String title) throws DocumentException {
        com.factura.facturacion.entidades.empresa.Empresa empresa = empresaRepositorio.findAll().stream().findFirst()
                .orElse(null);

        if (empresa != null) {
            Font fontEmpresa = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph pEmpresa = new Paragraph(empresa.getRazonSocial(), fontEmpresa);
            pEmpresa.setAlignment(Element.ALIGN_CENTER);
            document.add(pEmpresa);

            Paragraph pRuc = new Paragraph("RUC: " + empresa.getRuc());
            pRuc.setAlignment(Element.ALIGN_CENTER);
            document.add(pRuc);
        }

        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph pTitulo = new Paragraph(title, fontHeader);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitulo);
        document.add(new Paragraph("\n"));
    }
}
