package com.axlero.dashboard.controllers;

import com.axlero.dashboard.dto.ExportRequest;
import com.axlero.dashboard.services.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/export")
@Tag(name = "Export & Reports", description = "Export dashboard metrics to Excel (.xlsx) and CSV via Apache POI")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @PostMapping("/excel")
    @Operation(summary = "Export metrics data to Excel (.xlsx) spreadsheet")
    public ResponseEntity<byte[]> exportExcel(@RequestBody ExportRequest request) throws IOException {
        byte[] excelBytes = exportService.exportToExcel(request);
        String filename = (request.title() != null ? request.title().replaceAll("\\s+", "_") : "dashboard_export") + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @PostMapping("/csv")
    @Operation(summary = "Export metrics data to CSV format")
    public ResponseEntity<byte[]> exportCsv(@RequestBody ExportRequest request) {
        byte[] csvBytes = exportService.exportToCsv(request);
        String filename = (request.title() != null ? request.title().replaceAll("\\s+", "_") : "dashboard_export") + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }
}
