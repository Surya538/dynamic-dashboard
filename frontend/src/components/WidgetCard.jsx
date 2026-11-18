import React, { useEffect, useRef } from 'react';
import Chart from 'chart.js/auto';
import { Trash2, TrendingUp, Zap, Clock } from 'lucide-react';

export default function WidgetCard({ widget, result, loading, onDelete }) {
  const chartRef = useRef(null);
  const chartInstance = useRef(null);

  useEffect(() => {
    if (!result || widget.chartType === 'KPI' || !chartRef.current) return;

    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    const labels = result.buckets?.map((b) => b.dimensionValue) || [];
    const data = result.buckets?.map((b) => b.metricValue) || [];

    const palette = [
      '#6366f1', '#38bdf8', '#34d399', '#f59e0b', '#ec4899', '#8b5cf6', '#10b981'
    ];

    const type = widget.chartType.toLowerCase() === 'area' ? 'line' : widget.chartType.toLowerCase();

    const config = {
      type: type,
      data: {
        labels: labels,
        datasets: [
          {
            label: `${widget.metricType} of ${widget.metricField}`,
            data: data,
            backgroundColor: widget.chartType === 'PIE' ? palette : 'rgba(99, 102, 241, 0.7)',
            borderColor: widget.chartType === 'LINE' ? '#38bdf8' : '#6366f1',
            borderWidth: 2,
            fill: widget.chartType === 'AREA' || widget.chartType === 'LINE',
            tension: 0.35,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: widget.chartType === 'PIE',
            labels: { color: '#cbd5e1', font: { family: 'Plus Jakarta Sans', size: 11 } },
          },
        },
        scales:
          widget.chartType === 'PIE'
            ? {}
            : {
                x: {
                  ticks: { color: '#94a3b8', font: { size: 10 } },
                  grid: { color: 'rgba(51, 65, 85, 0.3)' },
                },
                y: {
                  ticks: { color: '#94a3b8', font: { size: 10 } },
                  grid: { color: 'rgba(51, 65, 85, 0.3)' },
                },
              },
      },
    };

    chartInstance.current = new Chart(chartRef.current, config);

    return () => {
      if (chartInstance.current) {
        chartInstance.current.destroy();
      }
    };
  }, [result, widget]);

  const isCacheHit = result && result.executionTimeMs < 40;

  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 shadow-xl flex flex-col justify-between hover:border-slate-700 transition">
      {/* Header */}
      <div className="flex items-start justify-between mb-4 border-b border-slate-800/80 pb-3">
        <div>
          <h3 className="font-bold text-white text-base tracking-tight">{widget.title}</h3>
          <div className="flex items-center space-x-2 mt-1">
            <span className="text-[11px] font-mono px-2 py-0.5 rounded bg-indigo-950 text-indigo-300 border border-indigo-800/60">
              {widget.metricType}({widget.metricField})
            </span>
            <span className="text-xs text-slate-500">by</span>
            <span className="text-xs font-semibold text-amber-400 capitalize">
              {widget.dimensionField}
            </span>
          </div>
        </div>

        <div className="flex items-center space-x-2">
          {result && (
            <div
              className={`flex items-center space-x-1 text-[11px] font-mono px-2 py-0.5 rounded-full border ${
                isCacheHit
                  ? 'bg-emerald-950/80 text-emerald-300 border-emerald-800/60'
                  : 'bg-slate-800 text-slate-300 border-slate-700'
              }`}
            >
              {isCacheHit ? <Zap className="h-3 w-3 text-emerald-400" /> : <Clock className="h-3 w-3 text-slate-400" />}
              <span>{result.executionTimeMs}ms</span>
            </div>
          )}
          {onDelete && (
            <button
              onClick={() => onDelete(widget.id)}
              className="p-1 rounded-lg text-slate-500 hover:text-rose-400 hover:bg-slate-800 transition"
              title="Delete Widget"
            >
              <Trash2 className="h-4 w-4" />
            </button>
          )}
        </div>
      </div>

      {/* Body / Chart */}
      <div className="h-60 flex items-center justify-center">
        {loading ? (
          <div className="flex flex-col items-center space-y-2 text-slate-500 text-xs">
            <div className="h-6 w-6 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin" />
            <span>Evaluating Criteria Query...</span>
          </div>
        ) : widget.chartType === 'KPI' ? (
          <div className="text-center py-4">
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1">
              Aggregated {widget.metricType}
            </p>
            <p className="text-4xl sm:text-5xl font-black text-white tracking-tight">
              {result ? Number(result.overallTotal).toLocaleString() : '0.00'}
            </p>
            <div className="mt-2 inline-flex items-center space-x-1 text-xs text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded-full">
              <TrendingUp className="h-3 w-3" />
              <span>{result?.buckets?.length || 0} Distinct Groups Processed</span>
            </div>
          </div>
        ) : (
          <div className="w-full h-full">
            <canvas ref={chartRef} />
          </div>
        )}
      </div>
    </div>
  );
}
