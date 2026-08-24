import { useState, useEffect } from 'react';

export default function Metrics() {
  const [stats, setStats] = useState({ total: 0, blocked: 0, redacted: 0 });

  useEffect(() => {
    fetch('http://localhost:8084/api/v1/audit-logs')
      .then(res => res.json())
      .then(data => {
        let blocked = 0;
        let redacted = 0;
        
        data.forEach(log => {
          if (log.eventType === 'BLOCK') blocked++;
          if (log.eventType === 'REDACTED') redacted++;
        });

        setStats({
          total: data.length,
          blocked,
          redacted
        });
      })
      .catch(console.error);
  }, []);

  return (
    <div className="metrics-grid">
      <div className="glass-panel metric-card">
        <span className="metric-title">Total API Requests</span>
        <span className="metric-value" style={{ color: 'var(--accent-blue)' }}>{stats.total}</span>
      </div>
      <div className="glass-panel metric-card">
        <span className="metric-title">Threats Blocked</span>
        <span className="metric-value" style={{ color: 'var(--status-block)' }}>{stats.blocked}</span>
      </div>
      <div className="glass-panel metric-card">
        <span className="metric-title">PII Redactions</span>
        <span className="metric-value" style={{ color: 'var(--status-redact)' }}>{stats.redacted}</span>
      </div>
    </div>
  );
}
