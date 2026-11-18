import React from 'react';
import { LayoutDashboard, Database, BookOpen, LogIn, LogOut, User, ShieldCheck } from 'lucide-react';

export default function Navbar({ user, onOpenAuth, onLogout }) {
  return (
    <header className="border-b border-slate-800 bg-slate-900/90 backdrop-blur sticky top-0 z-40 px-6 py-3.5">
      <div className="max-w-7xl mx-auto flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <div className="h-10 w-10 rounded-xl bg-gradient-to-tr from-indigo-600 to-violet-500 flex items-center justify-center text-white shadow-lg shadow-indigo-500/25">
            <LayoutDashboard className="h-5 w-5" />
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <h1 className="font-extrabold text-lg tracking-tight text-white">Axlero</h1>
              <span className="text-xs uppercase tracking-wider font-semibold px-2 py-0.5 rounded-full bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
                Data Platform
              </span>
            </div>
            <p className="text-xs text-slate-400">Dynamic Dashboard as a Service</p>
          </div>
        </div>

        <div className="flex items-center space-x-3">
          <a
            href="http://localhost:8080/swagger-ui.html"
            target="_blank"
            rel="noreferrer"
            className="hidden sm:flex items-center space-x-1.5 text-xs font-medium px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700/80 transition"
          >
            <BookOpen className="h-3.5 w-3.5 text-emerald-400" />
            <span>OpenAPI Docs</span>
          </a>

          <a
            href="http://localhost:8080/h2-console"
            target="_blank"
            rel="noreferrer"
            className="hidden sm:flex items-center space-x-1.5 text-xs font-medium px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700/80 transition"
          >
            <Database className="h-3.5 w-3.5 text-amber-400" />
            <span>H2 Console</span>
          </a>

          {user ? (
            <div className="flex items-center space-x-3 pl-2 border-l border-slate-800">
              <div className="flex items-center space-x-2 bg-slate-800/80 px-3 py-1.5 rounded-lg border border-slate-700/60">
                <ShieldCheck className="h-4 w-4 text-emerald-400" />
                <span className="text-xs font-medium text-slate-200">@{user.username}</span>
              </div>
              <button
                onClick={onLogout}
                className="flex items-center space-x-1 text-xs text-slate-400 hover:text-rose-400 px-2 py-1 transition"
              >
                <LogOut className="h-3.5 w-3.5" />
                <span>Logout</span>
              </button>
            </div>
          ) : (
            <button
              onClick={onOpenAuth}
              className="flex items-center space-x-1.5 text-xs font-semibold px-4 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg shadow-indigo-600/25 transition"
            >
              <LogIn className="h-3.5 w-3.5" />
              <span>Login / Register</span>
            </button>
          )}
        </div>
      </div>
    </header>
  );
}
