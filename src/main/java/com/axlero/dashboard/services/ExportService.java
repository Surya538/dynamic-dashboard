package com.axlero.dashboard.services;

import com.axlero.dashboard.dto.ExportRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Service to export dashboard and metric aggregation results
 * to Excel (.xlsx) using Apache POI and CSV formats.
 */
@Service
public class ExportService {

    public byte[] exportToExcel(ExportRequest request) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(request.title() != null ? request.title() : "Dashboard Data");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            List<String> headers = request.headers();
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            int rowIdx = 1;
            if (request.rows() != null) {
                for (List<String> rowData : request.rows()) {
                    Row row = sheet.createRow(rowIdx++);
                    for (int colIdx = 0; colIdx < rowData.size(); colIdx++) {
                        row.createCell(colIdx).setCellValue(rowData.get(colIdx));
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportToCsv(ExportRequest request) {
        StringBuilder csv = new StringBuilder();

        // Headers
        if (request.headers() != null && !request.headers().isEmpty()) {
            csv.append(String.join(",", request.headers().stream().map(this::escapeCsv).toList()));
            csv.append("\n");
        }

        // Data Rows
        if (request.rows() != null) {
            for (List<String> row : request.rows()) {
                csv.append(String.join(",", row.stream().map(this::escapeCsv).toList()));
                csv.append("\n");
            }
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
