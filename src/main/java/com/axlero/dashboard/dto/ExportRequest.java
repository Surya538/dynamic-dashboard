package com.axlero.dashboard.dto;

import java.util.List;

public record ExportRequest(
        String title,
        List<String> headers,
        List<List<String>> rows
) {}
