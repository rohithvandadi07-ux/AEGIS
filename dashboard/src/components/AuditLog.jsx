import { useState, useEffect } from 'react';

export default function AuditLog() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('http://localhost:8084/api/v1/audit-logs')
      .then(res => res.json())
      .then(data => {
        setLogs(data);
        setLoading(false);
      })
      .catch(err => {
        console.error("Failed to fetch logs", err);
        setLoading(false);
      });
  }, []);

  if (loading) return <div>Loading telemetry data...</div>;

  return (
    <div className="glass-panel" style={{ padding: '2rem' }}>
      <h2 style={{ marginBottom: '1.5rem', fontWeight: 600 }}>Recent Telemetry Events</h2>
      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Timestamp</th>
              <th>Tenant ID</th>
              <th>Event Type</th>
              <th>Latency (ms)</th>
              <th>Rules/Details</th>
            </tr>
          </thead>
          <tbody>
            {logs.map(log => (
              <tr key={log.id}>
                <td>{new Date(log.timestamp).toLocaleString()}</td>
                <td style={{ fontFamily: 'monospace' }}>{log.tenantId}</td>
                <td>
                  <span className={`badge ${log.eventType.toLowerCase()}`}>
                    {log.eventType}
                  </span>
                </td>
                <td>{log.latencyMs}ms</td>
                <td style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                  {log.matchedRules || '-'}
                </td>
              </tr>
            ))}
            {logs.length === 0 && (
              <tr>
                <td colSpan="5" style={{ textAlign: 'center', color: 'var(--text-secondary)' }}>
                  No telemetry events recorded yet.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
