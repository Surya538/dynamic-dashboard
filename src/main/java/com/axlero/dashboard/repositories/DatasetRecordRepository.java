package com.axlero.dashboard.repositories;

import com.axlero.dashboard.entities.DatasetRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRecordRepository extends JpaRepository<DatasetRecord, Long>, JpaSpecificationExecutor<DatasetRecord> {
    List<DatasetRecord> findByWorkspaceIdAndDatasetName(Long workspaceId, String datasetName);
    long countByWorkspaceId(Long workspaceId);
}
