package EmpleadoInterfaz;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Clase para generar documentos PDF de facturas con diseño personalizado.
 * <p>
 * Proporciona funcionalidad para crear facturas con:
 * - Encabezado y pie de página personalizados
 * - Logo de la empresa
 * - Tabla de productos con sus detalles
 * - Estilos y colores consistentes
 * - Apertura automática del PDF generado
 * </p>
 */
public class GenerarPDF {

    // Colores para el diseño del PDF
    private static final BaseColor COLOR_FONDO = new BaseColor(101, 201, 195);
    private static final BaseColor COLOR_FONDO_DOC = new BaseColor(225, 234, 236);

    /**
     * Clase interna para manejar eventos de página (header y footer).
     * <p>
     * Define el contenido que aparecerá en todas las páginas del documento PDF.
     * </p>
     */
    private static class PdfHeaderFooter extends PdfPageEventHelper {
        /**
         * Método que se ejecuta al finalizar cada página.
         *
         * @param writer El escritor PDF
         * @param document El documento PDF
         */
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // Header
            cb.setColorFill(COLOR_FONDO);
            cb.rectangle(0, document.top() + 10, document.getPageSize().getWidth(), 30);
            cb.fill();

            try {
                // Texto del header
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase("FARMASOFT - Sistema de Gestión Farmacéutica",
                                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)),
                        document.getPageSize().getWidth() / 2, document.top() + 20, 0);

                // Footer
                cb.setColorFill(COLOR_FONDO);
                cb.rectangle(0, 0, document.getPageSize().getWidth(), 30);
                cb.fill();

                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase("Gracias por su compra - Tel: 123-456-7890",
                                new Font(Font.FontFamily.HELVETICA, 10)),
                        document.getPageSize().getWidth() / 2, 15, 0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Genera un archivo PDF con la factura de una venta.
     * <p>
     * Crea un documento PDF profesional con todos los detalles de la venta,
     * incluyendo información del cliente, productos comprados y totales.
     * </p>
     *
     * @param idVenta ID de la venta a facturar
     * @param productos Lista de productos en la venta (formato: cliente|producto|cantidad|tipo|precio)
     * @param fecha Fecha de la venta
     */
    public void generarFacturaPDF(int idVenta, List<String> productos, String fecha) {
        try {
            // Configuración inicial del documento
            String directorio = System.getProperty("user.home") + "\\FacturasPinillos";
            if (!Files.exists(Paths.get(directorio))) {
                Files.createDirectories(Paths.get(directorio));
            }

            String ruta = directorio + "\\Venta_" + idVenta + ".pdf";

            Document documento = new Document();
            PdfWriter writer = PdfWriter.getInstance(documento, new FileOutputStream(ruta));

            // Configurar header, footer y fondo
            writer.setPageEvent(new PdfHeaderFooter() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    super.onEndPage(writer, document);
                    PdfContentByte canvas = writer.getDirectContentUnder();
                    canvas.saveState();
                    canvas.setColorFill(COLOR_FONDO_DOC);
                    canvas.rectangle(0, 0, document.getPageSize().getWidth(), document.getPageSize().getHeight());
                    canvas.fill();
                    canvas.restoreState();
                }
            });

            documento.open();

            // Agregar logo
            try {
                Image logo = Image.getInstance(getClass().getResource("/imagenes/PngLogo.png"));
                logo.scaleToFit(100, 100);
                logo.setAlignment(Element.ALIGN_RIGHT);
                documento.add(logo);
            } catch (Exception e) {
                System.out.println("No se pudo cargar el logo: " + e.getMessage());
            }

            // Título e información
            Paragraph titulo = new Paragraph("FACTURA #" + idVenta,
                    new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            Paragraph farmacia = new Paragraph("PHARMASOFT\nSENA CLEM, Tuluá Valle\nFecha: " + fecha,
                    new Font(Font.FontFamily.HELVETICA, 10));
            farmacia.setAlignment(Element.ALIGN_CENTER);
            documento.add(farmacia);

            documento.add(Chunk.NEWLINE);

            // Crear tabla con 5 columnas
            PdfPTable tabla = new PdfPTable(5); // Cliente, Producto, Cantidad, Tipo, Precio (IVA)
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(10f);
            tabla.setSpacingAfter(10f);

            // Encabezados de la tabla
            String[] encabezados = {"Cliente", "Producto", "Cantidad", "Tipo", "Precio (IVA)"};
            for (String encabezado : encabezados) {
                PdfPCell celda = new PdfPCell(new Phrase(encabezado,
                        new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
                celda.setBackgroundColor(COLOR_FONDO);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(5);
                tabla.addCell(celda);
            }

            // Agregar productos a la tabla
            boolean primeraFila = true;
            for (String producto : productos) {
                String[] datos = producto.split("\\|");

                // Para la primera fila mostrar el cliente, luego dejarlo en blanco
                PdfPCell celdaCliente = new PdfPCell(new Phrase(primeraFila ? datos[0] : ""));
                celdaCliente.setPadding(5);
                tabla.addCell(celdaCliente);

                // Resto de celdas
                for (int i = 1; i < datos.length; i++) {
                    PdfPCell celda = new PdfPCell(new Phrase(datos[i]));
                    celda.setPadding(5);
                    tabla.addCell(celda);
                }

                primeraFila = false;
            }

            documento.add(tabla);
            documento.close();

            // Abrir el PDF automáticamente
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new java.io.File(ruta));
            }

        } catch (Exception e) {
            System.err.println("Error al generar factura: " + e.getMessage());
            e.printStackTrace();
        }
    }
}