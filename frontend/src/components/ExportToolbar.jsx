import React, { useState } from 'react';
import { FileSpreadsheet, FileText, Download } from 'lucide-react';
import { exportApi } from '../services/api';

export default function ExportToolbar({ widgets, queryResults }) {
  const [exporting, setExporting] = useState(false);

  const handleExport = async (format) => {
    if (!widgets || widgets.length === 0) {
      alert('No widget data available to export.');
      return;
    }

    setExporting(true);
    try {
      const headers = ['Widget Title', 'Dimension Field', 'Dimension Value', 'Metric Value', 'Record Count'];
      const rows = [];

      widgets.forEach((widget) => {
        const result = queryResults[widget.id];
        if (result && result.buckets) {
          result.buckets.forEach((bucket) => {
            rows.push([
              widget.title,
              widget.dimensionField,
              bucket.dimensionValue,
              String(bucket.metricValue),
              String(bucket.recordCount),
            ]);
          });
        }
      });

      const payload = {
        title: 'Axlero_Enterprise_Metrics_Report',
        headers,
        rows,
      };

      const res = format === 'excel' ? await exportApi.excel(payload) : await exportApi.csv(payload);

      const blob = new Blob([res.data], {
        type:
          format === 'excel'
            ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            : 'text/csv',
      });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `axlero_metrics_${new Date().toISOString().slice(0, 10)}.${format === 'excel' ? 'xlsx' : 'csv'}`;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      alert('Export failed. Please check network connectivity.');
    } finally {
      setExporting(false);
    }
  };

  return (
    <div className="flex items-center space-x-2">
      <button
        onClick={() => handleExport('excel')}
        disabled={exporting}
        className="flex items-center space-x-1.5 px-3.5 py-2 rounded-xl bg-emerald-600/90 hover:bg-emerald-500 text-white text-xs font-semibold shadow-lg shadow-emerald-600/20 transition disabled:opacity-50"
      >
        <FileSpreadsheet className="h-4 w-4" />
        <span>Export Excel (.xlsx)</span>
      </button>

      <button
        onClick={() => handleExport('csv')}
        disabled={exporting}
        className="flex items-center space-x-1.5 px-3.5 py-2 rounded-xl bg-sky-600/90 hover:bg-sky-500 text-white text-xs font-semibold shadow-lg shadow-sky-600/20 transition disabled:opacity-50"
      >
        <FileText className="h-4 w-4" />
        <span>Export CSV</span>
      </button>
    </div>
  );
}
