import React, { useState } from 'react';
import { X, PlusCircle, Database } from 'lucide-react';
import { datasetApi } from '../services/api';

export default function DatasetRecordModal({ isOpen, onClose, workspaceId, onRecordAdded }) {
  const [category, setCategory] = useState('Electronics');
  const [region, setRegion] = useState('North America');
  const [status, setStatus] = useState('COMPLETED');
  const [numericValue, setNumericValue] = useState('5000.00');
  const [quantity, setQuantity] = useState(25);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await datasetApi.addRecord({
        workspaceId,
        datasetName: 'sales',
        category,
        region,
        status,
        numericValue: parseFloat(numericValue),
        quantity: parseInt(quantity, 10),
      });

      onRecordAdded();
      onClose();
    } catch (err) {
      setError(err.response?.data?.detail || 'Failed to insert dataset record');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-sm animate-in fade-in">
      <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-md w-full p-6 shadow-2xl relative">
        <button
          onClick={onClose}
          className="absolute right-4 top-4 p-1 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition"
        >
          <X className="h-5 w-5" />
        </button>

        <div className="mb-5 flex items-center space-x-2">
          <div className="p-2 rounded-lg bg-emerald-500/20 text-emerald-400">
            <Database className="h-5 w-5" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-white tracking-tight">Ingest Dataset Record</h2>
            <p className="text-xs text-slate-400">Inserts a transaction & tests auto cache invalidation</p>
          </div>
        </div>

        {error && (
          <div className="mb-4 p-2.5 rounded-lg bg-rose-500/10 border border-rose-500/20 text-rose-400 text-xs">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-3.5 text-sm">
          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Category</label>
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-white focus:outline-none focus:border-indigo-500"
            >
              <option value="Electronics">Electronics</option>
              <option value="Software">Software</option>
              <option value="Furniture">Furniture</option>
              <option value="Cloud Services">Cloud Services</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Region</label>
            <select
              value={region}
              onChange={(e) => setRegion(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-white focus:outline-none focus:border-indigo-500"
            >
              <option value="North America">North America</option>
              <option value="Europe">Europe</option>
              <option value="Asia-Pacific">Asia-Pacific</option>
              <option value="Latin America">Latin America</option>
              <option value="Middle East">Middle East</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Status</label>
            <select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-white focus:outline-none focus:border-indigo-500"
            >
              <option value="COMPLETED">COMPLETED</option>
              <option value="PENDING">PENDING</option>
              <option value="CANCELLED">CANCELLED</option>
            </select>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">Revenue ($)</label>
              <input
                type="number"
                step="0.01"
                required
                value={numericValue}
                onChange={(e) => setNumericValue(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-white focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">Quantity (Units)</label>
              <input
                type="number"
                required
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-white focus:outline-none focus:border-indigo-500"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full mt-4 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-sm shadow-lg shadow-emerald-600/25 transition flex items-center justify-center space-x-2"
          >
            <PlusCircle className="h-4 w-4" />
            <span>{loading ? 'Saving...' : 'Add Transaction'}</span>
          </button>
        </form>
      </div>
    </div>
  );
}
