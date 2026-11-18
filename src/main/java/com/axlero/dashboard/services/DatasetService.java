package com.axlero.dashboard.services;

import com.axlero.dashboard.dto.DatasetDtos.DatasetSummary;
import com.axlero.dashboard.dto.DatasetDtos.RecordCreateRequest;
import com.axlero.dashboard.entities.DatasetRecord;
import com.axlero.dashboard.entities.Workspace;
import com.axlero.dashboard.repositories.DatasetRecordRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DatasetService {

    private final DatasetRecordRepository datasetRepository;
    private final WorkspaceService workspaceService;

    public DatasetService(DatasetRecordRepository datasetRepository, WorkspaceService workspaceService) {
        this.datasetRepository = datasetRepository;
        this.workspaceService = workspaceService;
    }

    @Transactional
    @CacheEvict(value = "metricsCache", allEntries = true)
    public DatasetRecord addRecord(RecordCreateRequest request, String username) {
        Workspace workspace = workspaceService.getEntityById(request.workspaceId());
        workspaceService.validateOwnership(workspace, username);

        DatasetRecord record = new DatasetRecord(
                workspace,
                request.datasetName() != null ? request.datasetName() : "sales",
                request.category(),
                request.region(),
                request.status(),
                request.numericValue(),
                request.quantity() != null ? request.quantity() : 1,
                request.recordDate() != null ? request.recordDate() : LocalDate.now()
        );

        return datasetRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<DatasetRecord> getRecordsByWorkspace(Long workspaceId, String datasetName, String username) {
        Workspace workspace = workspaceService.getEntityById(workspaceId);
        workspaceService.validateOwnership(workspace, username);

        return datasetRepository.findByWorkspaceIdAndDatasetName(workspaceId, datasetName != null ? datasetName : "sales");
    }
}
