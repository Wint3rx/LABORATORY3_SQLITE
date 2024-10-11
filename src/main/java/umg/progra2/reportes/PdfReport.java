package umg.progra2.reportes;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import umg.progra2.DaseDatos.model.Producto;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class PdfReport {
    private static final Font TITLE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLDITALIC);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

    public void generateProductReport(List<Producto> productos, String outputPath) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));
        document.open();

        addTitle(document);
        addProductTable(document, productos);
        try {
            addQrCode(document, "https://www.google.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        document.close();
    }

    private void addQrCode(Document document, String qrCodeData) throws DocumentException, Exception {

        BitMatrix bitMatrix = new MultiFormatWriter().encode(qrCodeData, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);


        Image qrImage = Image.getInstance(pngOutputStream.toByteArray());
        qrImage.setAlignment(Element.ALIGN_CENTER);
        document.add(qrImage);
    }

    private void addTitle(Document document) throws DocumentException {
        Paragraph title = new Paragraph("Reporte de Productos \nFranklin Boanerges Lopez Chavarria \n0905-23-4498", TITLE_FONT);

        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);
    }

    private void addProductTable(Document document, List<Producto> productos) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        addTableHeader(table);
        addRowsGroupedByOrigin(table, productos);
        document.add(table);
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("ID", "Nombre", "Pais", "Precio", "Existencia", "Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.MAGENTA);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, HEADER_FONT));
                    table.addCell(header);
                });
    }

    private void addRowsGroupedByOrigin(PdfPTable table, List<Producto> productos) {
        String currentOrigin = null;
        double groupTotalPrice = 0.0;
        int groupTotalQuantity = 0;
        double grandTotalPrice = 0.0;
        int grandTotalQuantity = 0;

        for (Producto producto : productos) {
            System.out.println("Procesando producto: " + producto.getNombre() + ", Pais: " + producto.getPais()); // Debug

            if (currentOrigin == null || !producto.getPais().equals(currentOrigin)) {

                if (currentOrigin != null) {
                    addGroupTotalRow(table, currentOrigin, groupTotalPrice, groupTotalQuantity);
                    grandTotalPrice += groupTotalPrice;
                    grandTotalQuantity += groupTotalQuantity;
                }

                currentOrigin = producto.getPais();
                groupTotalPrice = 0.0;
                groupTotalQuantity = 0;

                PdfPCell groupCell = new PdfPCell(new Phrase("Grupo: " + currentOrigin, NORMAL_FONT));
                groupCell.setColspan(6);
                groupCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(groupCell);
            }

            double total = producto.getPrecio() * producto.getExistencia();
            table.addCell(new Phrase(String.valueOf(producto.getIdProducto()), NORMAL_FONT));
            table.addCell(new Phrase(producto.getNombre(), NORMAL_FONT));
            table.addCell(new Phrase(producto.getPais(), NORMAL_FONT));
            table.addCell(new Phrase(String.format("Q%.2f", producto.getPrecio()), NORMAL_FONT));
            table.addCell(new Phrase(String.valueOf(producto.getExistencia()), NORMAL_FONT));
            table.addCell(new Phrase(String.format("Q%.2f", total), NORMAL_FONT));

            groupTotalPrice += total;
            groupTotalQuantity += producto.getExistencia();
        }


        if (currentOrigin != null) {
            addGroupTotalRow(table, currentOrigin, groupTotalPrice, groupTotalQuantity);
            grandTotalPrice += groupTotalPrice;
            grandTotalQuantity += groupTotalQuantity;
        }


        addGrandTotalRow(table, grandTotalPrice, grandTotalQuantity);
    }

    private void addGrandTotalRow(PdfPTable table, double totalPrice, int totalQuantity) {
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total Final", HEADER_FONT));
        totalLabelCell.setColspan(3);
        totalLabelCell.setBackgroundColor(BaseColor.CYAN);
        table.addCell(totalLabelCell);
        table.addCell(new Phrase(String.format("Q%.2f", totalPrice), HEADER_FONT));
        table.addCell(new Phrase(String.valueOf(totalQuantity), HEADER_FONT));
        table.addCell(new Phrase("", HEADER_FONT)); // Columna vacía para que la tabla se mantenga balanceada
    }



    private void addGroupTotalRow(PdfPTable table, String origin, double totalPrice, int totalQuantity) {
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total del Grupo: " + origin, HEADER_FONT));
        totalLabelCell.setColspan(3);
        totalLabelCell.setBackgroundColor(BaseColor.CYAN);
        table.addCell(totalLabelCell);
        table.addCell(new Phrase(String.format("Q%.2f", totalPrice), HEADER_FONT));
        table.addCell(new Phrase(String.valueOf(totalQuantity), HEADER_FONT));
        table.addCell(new Phrase("", HEADER_FONT));
    }
}
