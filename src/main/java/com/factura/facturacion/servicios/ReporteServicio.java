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

    public ReporteServicio(ClienteRepositorio clienteRepositorio, ProductoRepositorio productoRepositorio,
            FacturaRepositorio facturaRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.facturaRepositorio = facturaRepositorio;
    }

    public byte[] generarReporteClientes() throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titulo = new Paragraph("Reporte de Clientes", fontHeader);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph("\n"));

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

        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titulo = new Paragraph("Reporte de Productos", fontHeader);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph("\n"));

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

        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titulo = new Paragraph("Reporte de Facturas", fontHeader);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.addCell(getHeaderCell("Secuencial"));
        table.addCell(getHeaderCell("Fecha"));
        table.addCell(getHeaderCell("Cliente"));
        table.addCell(getHeaderCell("Total"));

        List<Factura> facturas = facturaRepositorio.findAll();
        for (Factura factura : facturas) {
            table.addCell(factura.getSecuencial());
            table.addCell(factura.getFechaEmision().toString());
            table.addCell(factura.getRazonSocialComprador());
            table.addCell(factura.getImporteTotal().toString());
        }

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
}
