import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import WidgetCard from './components/WidgetCard';
import AuthModal from './components/AuthModal';
import WidgetBuilderModal from './components/WidgetBuilderModal';
import DatasetRecordModal from './components/DatasetRecordModal';
import ExportToolbar from './components/ExportToolbar';
import { metricApi, authApi, dashboardApi } from './services/api';
import { Plus, RefreshCw, Zap, Shield, Database, PlusCircle } from 'lucide-react';

const initialWidgets = [
  { id: 1, title: 'Revenue by Region', chartType: 'BAR', metricType: 'SUM', dimensionField: 'region', metricField: 'numericValue' },
  { id: 2, title: 'Sales by Product Category', chartType: 'PIE', metricType: 'SUM', dimensionField: 'category', metricField: 'numericValue' },
  { id: 3, title: 'Transaction Distribution by Status', chartType: 'LINE', metricType: 'COUNT', dimensionField: 'status', metricField: 'numericValue' },
  { id: 4, title: 'Total Volume of Units Sold', chartType: 'KPI', metricType: 'SUM', dimensionField: 'status', metricField: 'quantity' },
];

export default function App() {
  const [user, setUser] = useState(null);
  const [widgets, setWidgets] = useState(initialWidgets);
  const [queryResults, setQueryResults] = useState({});
  const [loadingWidgets, setLoadingWidgets] = useState({});
  const [avgExecutionTime, setAvgExecutionTime] = useState(12);

  // Modals
  const [isAuthOpen, setIsAuthOpen] = useState(false);
  const [isWidgetBuilderOpen, setIsWidgetBuilderOpen] = useState(false);
  const [isDataModalOpen, setIsDataModalOpen] = useState(false);

  useEffect(() => {
    const storedUser = localStorage.getItem('jwt_user');
    const token = localStorage.getItem('jwt_token');

    if (token && storedUser) {
      setUser(JSON.parse(storedUser));
      fetchMetricsForAll(widgets);
    } else {
      // Auto-authenticate with seeded demo user
      handleAutoLogin();
    }
  }, []);

  const handleAutoLogin = async () => {
    try {
      const res = await authApi.login({ username: 'demouser', password: 'password123' });
      const { token, username, role } = res.data;
      localStorage.setItem('jwt_token', token);
      localStorage.setItem('jwt_user', JSON.stringify({ username, role }));
      setUser({ username, role });
      fetchMetricsForAll(widgets);
    } catch (e) {
      console.warn('Initial auto-login waiting for user interaction');
      fetchMetricsForAll(widgets);
    }
  };

  const fetchMetric = async (widget) => {
    setLoadingWidgets((prev) => ({ ...prev, [widget.id]: true }));
    try {
      const payload = {
        workspaceId: 1,
        datasetName: 'sales',
        dimensionField: widget.dimensionField,
        metricField: widget.metricField,
        aggregation: widget.metricType,
        filters: {},
        limit: 15,
      };

      const res = await metricApi.aggregate(payload);
      setQueryResults((prev) => ({ ...prev, [widget.id]: res.data }));
      return res.data.executionTimeMs;
    } catch (err) {
      console.error(`Failed to fetch metric for widget ${widget.id}:`, err);
    } finally {
      setLoadingWidgets((prev) => ({ ...prev, [widget.id]: false }));
    }
    return null;
  };

  const fetchMetricsForAll = async (currentWidgets) => {
    const times = [];
    for (const widget of currentWidgets) {
      const time = await fetchMetric(widget);
      if (time !== null) times.push(time);
    }
    if (times.length > 0) {
      const avg = Math.round(times.reduce((a, b) => a + b, 0) / times.length);
      setAvgExecutionTime(avg);
    }
  };

  const handleAddWidget = (newWidgetConfig) => {
    const newWidget = {
      ...newWidgetConfig,
      id: Date.now(),
    };
    const updated = [...widgets, newWidget];
    setWidgets(updated);
    fetchMetric(newWidget);
  };

  const handleDeleteWidget = (id) => {
    setWidgets(widgets.filter((w) => w.id !== id));
    setQueryResults((prev) => {
      const copy = { ...prev };
      delete copy[id];
      return copy;
    });
  };

  const handleLogout = () => {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('jwt_user');
    setUser(null);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col">
      <Navbar
        user={user}
        onOpenAuth={() => setIsAuthOpen(true)}
        onLogout={handleLogout}
      />

      <main className="flex-1 max-w-7xl w-full mx-auto px-6 py-8">
        {/* Workspace Banner */}
        <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-5 bg-gradient-to-r from-slate-900 to-slate-900/80 p-6 rounded-3xl border border-slate-800 shadow-2xl mb-8">
          <div>
            <div className="flex items-center space-x-2 text-xs font-semibold text-slate-400 mb-1.5 uppercase tracking-wider">
              <span>TENANT WORKSPACE:</span>
              <span className="text-indigo-400 font-bold bg-indigo-500/10 px-2 py-0.5 rounded-full border border-indigo-500/20">
                Axlero Global Analytics
              </span>
            </div>
            <h2 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
              Executive Sales & Metric Overview
            </h2>
            <p className="text-xs text-slate-400 mt-1">
              Multi-dimensional analytical queries executed dynamically via Spring Data JPA Criteria API
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <button
              onClick={() => fetchMetricsForAll(widgets)}
              className="flex items-center space-x-1.5 px-3.5 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold border border-slate-700/80 transition"
            >
              <RefreshCw className="h-4 w-4" />
              <span>Refresh Metrics</span>
            </button>

            <button
              onClick={() => setIsDataModalOpen(true)}
              className="flex items-center space-x-1.5 px-3.5 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-emerald-400 text-xs font-semibold border border-slate-700/80 transition"
            >
              <PlusCircle className="h-4 w-4" />
              <span>Ingest Record</span>
            </button>

            <ExportToolbar widgets={widgets} queryResults={queryResults} />

            <button
              onClick={() => setIsWidgetBuilderOpen(true)}
              className="flex items-center space-x-1.5 px-4 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold shadow-lg shadow-indigo-600/30 transition"
            >
              <Plus className="h-4 w-4" />
              <span>Add Widget</span>
            </button>
          </div>
        </div>

        {/* System Performance Badges */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl flex items-center space-x-3.5">
            <div className="p-2.5 rounded-xl bg-emerald-500/10 text-emerald-400">
              <Zap className="h-5 w-5" />
            </div>
            <div>
              <p className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Caffeine Latency</p>
              <p className="text-lg font-bold text-emerald-400 font-mono">&lt; {avgExecutionTime}ms (Hit)</p>
            </div>
          </div>

          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl flex items-center space-x-3.5">
            <div className="p-2.5 rounded-xl bg-indigo-500/10 text-indigo-400">
              <Database className="h-5 w-5" />
            </div>
            <div>
              <p className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Query Engine</p>
              <p className="text-lg font-bold text-indigo-300">JPA Criteria API</p>
            </div>
          </div>

          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl flex items-center space-x-3.5">
            <div className="p-2.5 rounded-xl bg-amber-500/10 text-amber-400">
              <Shield className="h-5 w-5" />
            </div>
            <div>
              <p className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Security Model</p>
              <p className="text-lg font-bold text-amber-400">Stateless JWT + RBAC</p>
            </div>
          </div>

          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl flex items-center space-x-3.5">
            <div className="p-2.5 rounded-xl bg-sky-500/10 text-sky-400">
              <RefreshCw className="h-5 w-5" />
            </div>
            <div>
              <p className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Active Widgets</p>
              <p className="text-lg font-bold text-sky-400">{widgets.length} Rendered</p>
            </div>
          </div>
        </div>

        {/* Dynamic Widgets Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {widgets.map((widget) => (
            <WidgetCard
              key={widget.id}
              widget={widget}
              result={queryResults[widget.id]}
              loading={loadingWidgets[widget.id]}
              onDelete={handleDeleteWidget}
            />
          ))}
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-800/80 py-6 text-center text-xs text-slate-500">
        <p>Axlero Java Development (Spring Boot) SDE Internship Deliverable</p>
        <p className="mt-1 text-[11px] text-slate-600 font-mono">
          Layered Architecture • Java 17 Records • JPA Criteria API • Caffeine Caching • Apache POI
        </p>
      </footer>

      {/* Modals */}
      <AuthModal
        isOpen={isAuthOpen}
        onClose={() => setIsAuthOpen(false)}
        onAuthSuccess={(u) => {
          setUser(u);
          fetchMetricsForAll(widgets);
        }}
      />

      <WidgetBuilderModal
        isOpen={isWidgetBuilderOpen}
        onClose={() => setIsWidgetBuilderOpen(false)}
        onAddWidget={handleAddWidget}
      />

      <DatasetRecordModal
        isOpen={isDataModalOpen}
        onClose={() => setIsDataModalOpen(false)}
        workspaceId={1}
        onRecordAdded={() => fetchMetricsForAll(widgets)}
      />
    </div>
  );
}
