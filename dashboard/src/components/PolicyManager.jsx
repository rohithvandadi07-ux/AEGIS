import { useState, useEffect } from 'react';

export default function PolicyManager() {
  const [tenantId, setTenantId] = useState('default-tenant');
  const [disableHeuristics, setDisableHeuristics] = useState(false);
  const [disablePii, setDisablePii] = useState(false);
  const [saved, setSaved] = useState(false);

  const loadPolicy = () => {
    fetch(`http://localhost:8083/api/v1/policies/${tenantId}`)
      .then(res => {
        if (!res.ok) throw new Error("Not found");
        return res.json();
      })
      .then(data => {
        try {
          const config = JSON.parse(data.config || '{}');
          setDisableHeuristics(!!config.disableHeuristics);
          setDisablePii(!!config.disablePii);
        } catch (e) {
          console.error(e);
        }
      })
      .catch(() => {
        setDisableHeuristics(false);
        setDisablePii(false);
      });
  };

  useEffect(() => {
    loadPolicy();
  }, [tenantId]);

  const savePolicy = () => {
    const policy = {
      tenantId,
      enabled: true,
      config: JSON.stringify({ disableHeuristics, disablePii })
    };

    fetch('http://localhost:8083/api/v1/policies', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(policy)
    })
    .then(res => res.json())
    .then(() => {
      setSaved(true);
      setTimeout(() => setSaved(false), 3000);
    });
  };

  return (
    <div className="glass-panel policy-card">
      <h2 style={{ marginBottom: '1.5rem', fontWeight: 600 }}>Tenant Security Policy</h2>
      
      <div className="form-group">
        <label>Tenant ID</label>
        <input 
          type="text" 
          value={tenantId} 
          onChange={(e) => setTenantId(e.target.value)} 
        />
      </div>

      <div className="toggle-container">
        <input 
          type="checkbox" 
          id="disableHeuristics" 
          checked={disableHeuristics}
          onChange={(e) => setDisableHeuristics(e.target.checked)}
          style={{ transform: 'scale(1.5)', cursor: 'pointer' }}
        />
        <label htmlFor="disableHeuristics" style={{ cursor: 'pointer' }}>
          Disable Layer 1 & 2 Prompt Inspection (Heuristics & Semantics)
        </label>
      </div>

      <div className="toggle-container">
        <input 
          type="checkbox" 
          id="disablePii" 
          checked={disablePii}
          onChange={(e) => setDisablePii(e.target.checked)}
          style={{ transform: 'scale(1.5)', cursor: 'pointer' }}
        />
        <label htmlFor="disablePii" style={{ cursor: 'pointer' }}>
          Disable Outbound PII Redaction (Data Loss Prevention)
        </label>
      </div>

      <button className="btn" onClick={savePolicy} style={{ marginTop: '1rem' }}>
        {saved ? 'Policy Saved!' : 'Save Policy'}
      </button>
    </div>
  );
}
