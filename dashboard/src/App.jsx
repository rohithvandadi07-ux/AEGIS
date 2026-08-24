import { useState } from 'react';
import AuditLog from './components/AuditLog';
import PolicyManager from './components/PolicyManager';
import Metrics from './components/Metrics';

function App() {
  const [activeTab, setActiveTab] = useState('overview');

  return (
    <div className="app-container">
      <div className="sidebar">
        <div>
          <h1 style={{ letterSpacing: '2px', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{ color: 'var(--accent-blue)' }}>
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
            </svg>
            AEGIS
          </h1>
          <p style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', marginTop: '0.25rem', textTransform: 'uppercase', letterSpacing: '1px' }}>
            Security Gateway
          </p>
        </div>

        <nav className="nav-links">
          <div 
            className={`nav-item ${activeTab === 'overview' ? 'active' : ''}`}
            onClick={() => setActiveTab('overview')}
          >
            Dashboard Overview
          </div>
          <div 
            className={`nav-item ${activeTab === 'policies' ? 'active' : ''}`}
            onClick={() => setActiveTab('policies')}
          >
            Tenant Policies
          </div>
        </nav>
      </div>

      <div className="main-content">
        {activeTab === 'overview' ? (
          <>
            <h2 className="page-title">Security Telemetry</h2>
            <Metrics />
            <AuditLog />
          </>
        ) : (
          <>
            <h2 className="page-title">Policy Management</h2>
            <PolicyManager />
          </>
        )}
      </div>
    </div>
  );
}

export default App;
