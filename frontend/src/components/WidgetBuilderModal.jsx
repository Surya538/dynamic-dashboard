import React, { useState } from 'react';
import { X, Sparkles, BarChart2 } from 'lucide-react';

export default function WidgetBuilderModal({ isOpen, onClose, onAddWidget }) {
  const [title, setTitle] = useState('');
  const [chartType, setChartType] = useState('BAR');
  const [metricType, setMetricType] = useState('SUM');
  const [dimensionField, setDimensionField] = useState('region');
  const [metricField, setMetricField] = useState('numericValue');
  const [refreshInterval, setRefreshInterval] = useState(30);

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    onAddWidget({
      title: title || `${metricType} of ${metricField} by ${dimensionField}`,
      chartType,
      metricType,
      dimensionField,
      metricField,
      datasetName: 'sales',
      refreshInterval: Number(refreshInterval),
    });
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-sm animate-in fade-in">
      <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-lg w-full p-6 shadow-2xl relative">
        <button
          onClick={onClose}
          className="absolute right-4 top-4 p-1 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition"
        >
          <X className="h-5 w-5" />
        </button>

        <div className="mb-6 flex items-center space-x-2">
          <div className="p-2 rounded-lg bg-indigo-600/20 text-indigo-400">
            <BarChart2 className="h-5 w-5" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-white tracking-tight">Create Dynamic Widget</h2>
            <p className="text-xs text-slate-400">Build an analytical query widget powered by Criteria API</p>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4 text-sm">
          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1.5">Widget Title</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g., Regional Sales Performance"
              className="w-full bg-slate-950 border border-slate-800 focus:border-indigo-500 rounded-xl px-3 py-2 text-white focus:outline-none transition"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">Chart Visualization</label>
              <select
                value={chartType}
                onChange={(e) => setChartType(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 focus:border-indigo-500 rounded-xl px-3 py-2 text-white focus:outline-none transition"
              >
                <option value="BAR">Bar Chart</option>
                <option value="LINE">Line Graph</option>
                <option value="PIE">Pie Chart</option>
                <option value="AREA">Area Chart</option>
                <option value="KPI">KPI Metric Card</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">Aggregation Operation</label>
              <select
                value={metricType}
                onChange={(e) => setMetricType(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 focus:border-indigo-500 rounded-xl px-3 py-2 text-white focus:outline-none transition"
              >
                <option value="SUM">SUM (Total Amount)</option>
                <option value="COUNT">COUNT (Row Frequency)</option>
                <option value="AVG">AVG (Average Value)</option>
                <option value="MIN">MIN (Minimum Value)</option>
                <option value="MAX">MAX (Maximum Value)</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">Grouping Dimension (GROUP BY)</label>
              <select
                value={dimensionField}
                onChange={(e) => setDimensionField(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 focus:border-indigo-500 rounded-xl px-3 py-2 text-white focus:outline-none transition"
              >
                <option value="region">Region (Geography)</option>
                <option value="category">Category (Product Type)</option>
                <option value="status">Status (Order State)</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">Metric Target Column</label>
              <select
                value={metricField}
                onChange={(e) => setMetricField(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 focus:border-indigo-500 rounded-xl px-3 py-2 text-white focus:outline-none transition"
              >
                <option value="numericValue">Revenue / Numeric Value</option>
                <option value="quantity">Units Quantity</option>
              </select>
            </div>
          </div>

          <button
            type="submit"
            className="w-full mt-4 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold shadow-lg shadow-indigo-600/30 transition flex items-center justify-center space-x-2"
          >
            <Sparkles className="h-4 w-4" />
            <span>Construct Dynamic Widget</span>
          </button>
        </form>
      </div>
    </div>
  );
}
