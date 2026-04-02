package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExportService {

    public byte[] exportProductsExcel(List<ProductResponse> products) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Prodotti");
            Row header = sheet.createRow(0);
            String[] headers = {"SKU", "Nome", "Categoria", "Fornitore", "Disponibile", "Scorta minima", "Posizione"};
            for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);
            int rowIdx = 1;
            for (ProductResponse p : products) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(nvl(p.getSku()));
                row.createCell(1).setCellValue(nvl(p.getName()));
                row.createCell(2).setCellValue(nvl(p.getCategory()));
                row.createCell(3).setCellValue(nvl(p.getSupplier()));
                row.createCell(4).setCellValue(p.getQuantityAvailable());
                row.createCell(5).setCellValue(p.getMinimumThreshold());
                row.createCell(6).setCellValue(nvl(p.getWarehouseLocation()));
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Errore export Excel prodotti", ex);
        }
    }

    public byte[] exportMovementsExcel(List<StockMovementResponse> movements) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Movimenti");
            Row header = sheet.createRow(0);
            String[] headers = {"Data", "SKU", "Prodotto", "Tipo", "Quantità", "Operatore", "Origine", "Destinazione", "Note"};
            for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);
            int rowIdx = 1;
            for (StockMovementResponse m : movements) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(String.valueOf(m.getCreatedAt()));
                row.createCell(1).setCellValue(nvl(m.getSku()));
                row.createCell(2).setCellValue(nvl(m.getProductName()));
                row.createCell(3).setCellValue(String.valueOf(m.getMovementType()));
                row.createCell(4).setCellValue(m.getQuantity());
                row.createCell(5).setCellValue(nvl(m.getPerformedBy()));
                row.createCell(6).setCellValue(nvl(m.getSourceLocation()));
                row.createCell(7).setCellValue(nvl(m.getDestinationLocation()));
                row.createCell(8).setCellValue(nvl(m.getNotes()));
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Errore export Excel movimenti", ex);
        }
    }

    public byte[] exportOrdersExcel(List<PurchaseOrderResponse> orders) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Ordini");
            Row header = sheet.createRow(0);
            String[] headers = {"ID", "Fornitore", "Stato", "Creato da", "Data", "Righe"};
            for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);
            int rowIdx = 1;
            for (PurchaseOrderResponse o : orders) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(o.getId());
                row.createCell(1).setCellValue(nvl(o.getSupplier()));
                row.createCell(2).setCellValue(String.valueOf(o.getStatus()));
                row.createCell(3).setCellValue(nvl(o.getCreatedBy()));
                row.createCell(4).setCellValue(String.valueOf(o.getCreatedAt()));
                row.createCell(5).setCellValue(o.getItems().stream()
                        .map(item -> item.getSku() + " x" + item.getQuantity())
                        .reduce((a, b) -> a + "; " + b).orElse(""));
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Errore export Excel ordini", ex);
        }
    }

    public byte[] exportProductsPdf(List<ProductResponse> products) {
        return buildPdf("Report Prodotti", new String[]{"SKU", "Nome", "Categoria", "Fornitore", "Disp.", "Min."},
                products.stream().map(p -> new String[]{
                        nvl(p.getSku()), nvl(p.getName()), nvl(p.getCategory()), nvl(p.getSupplier()),
                        String.valueOf(p.getQuantityAvailable()), String.valueOf(p.getMinimumThreshold())
                }).toList());
    }

    public byte[] exportMovementsPdf(List<StockMovementResponse> movements) {
        return buildPdf("Report Movimenti", new String[]{"Data", "SKU", "Tipo", "Qta", "Operatore"},
                movements.stream().map(m -> new String[]{
                        String.valueOf(m.getCreatedAt()), nvl(m.getSku()), String.valueOf(m.getMovementType()),
                        String.valueOf(m.getQuantity()), nvl(m.getPerformedBy())
                }).toList());
    }

    public byte[] exportOrdersPdf(List<PurchaseOrderResponse> orders) {
        return buildPdf("Report Ordini", new String[]{"ID", "Fornitore", "Stato", "Creato da", "Data"},
                orders.stream().map(o -> new String[]{
                        String.valueOf(o.getId()), nvl(o.getSupplier()), String.valueOf(o.getStatus()),
                        nvl(o.getCreatedBy()), String.valueOf(o.getCreatedAt())
                }).toList());
    }

    private byte[] buildPdf(String title, String[] headers, List<String[]> rows) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            for (String header : headers) table.addCell(header);
            for (String[] row : rows) {
                for (String value : row) table.addCell(nvl(value));
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Errore export PDF", ex);
        }
    }

    private String nvl(String value) {
        return value == null ? "" : value;
    }
}
