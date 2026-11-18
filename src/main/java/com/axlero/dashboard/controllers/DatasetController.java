package com.axlero.dashboard.controllers;

import com.axlero.dashboard.dto.DatasetDtos.RecordCreateRequest;
import com.axlero.dashboard.entities.DatasetRecord;
import com.axlero.dashboard.services.DatasetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/datasets")
@Tag(name = "Datasets & Data Ingestion", description = "Endpoints to manage and ingest multi-tenant dataset records")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @PostMapping("/records")
    @Operation(summary = "Add a new data record to a workspace dataset (auto-invalidates metrics cache)")
    public ResponseEntity<DatasetRecord> addRecord(@Valid @RequestBody RecordCreateRequest request,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        DatasetRecord created = datasetService.addRecord(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/workspace/{workspaceId}")
    @Operation(summary = "List dataset records for a workspace")
    public ResponseEntity<List<DatasetRecord>> getRecords(@PathVariable Long workspaceId,
                                                          @RequestParam(defaultValue = "sales") String datasetName,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        List<DatasetRecord> records = datasetService.getRecordsByWorkspace(workspaceId, datasetName, userDetails.getUsername());
        return ResponseEntity.ok(records);
    }
}
