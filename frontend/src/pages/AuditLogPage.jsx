import { useEffect, useState } from 'react'
import client from '../api/client'

export default function AuditLogPage() {
  const [logs, setLogs] = useState([])
  const [filters, setFilters] = useState({ dateFrom: '', dateTo: '', entityType: '', performedBy: '' })

  const load = async () => {
    const params = Object.fromEntries(Object.entries(filters).filter(([, value]) => value))
    const { data } = await client.get('/audit-logs', { params })
    setLogs(data)
  }

  useEffect(() => {
    load()
  }, [])

  return (
    <div className="stack-gap">
      <section className="card">
        <div className="section-head">
          <h2>Audit log</h2>
        </div>
        <form className="stack-form" onSubmit={(e) => { e.preventDefault(); load() }}>
          <div className="inline-form">
            <input type="date" value={filters.dateFrom} onChange={(e) => setFilters({ ...filters, dateFrom: e.target.value })} />
            <input type="date" value={filters.dateTo} onChange={(e) => setFilters({ ...filters, dateTo: e.target.value })} />
          </div>
          <input placeholder="Tipo entità (Product, PurchaseOrder...)" value={filters.entityType} onChange={(e) => setFilters({ ...filters, entityType: e.target.value })} />
          <input placeholder="Operatore" value={filters.performedBy} onChange={(e) => setFilters({ ...filters, performedBy: e.target.value })} />
          <button type="submit">Applica filtri</button>
        </form>
      </section>

      <section className="card">
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Data</th>
                <th>Azione</th>
                <th>Entità</th>
                <th>ID</th>
                <th>Operatore</th>
                <th>Ruolo</th>
                <th>Prima</th>
                <th>Dopo</th>
                <th>Note</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr key={log.id}>
                  <td>{new Date(log.createdAt).toLocaleString('it-IT')}</td>
                  <td>{log.action}</td>
                  <td>{log.entityType}</td>
                  <td>{log.entityId}</td>
                  <td>{log.performedBy}</td>
                  <td>{log.role}</td>
                  <td>{log.oldValue}</td>
                  <td>{log.newValue}</td>
                  <td>{log.notes}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}
