import { useEffect, useMemo, useState } from 'react'
import client from '../api/client'

const createFormInitial = {
  name: '',
  category: '',
  supplier: '',
  location: '',
  notes: '',
  applyAdjustmentsOnComplete: true,
}

export default function InventoryPage() {
  const [sessions, setSessions] = useState([])
  const [selectedSessionId, setSelectedSessionId] = useState('')
  const [selectedSession, setSelectedSession] = useState(null)
  const [createForm, setCreateForm] = useState(createFormInitial)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [countValues, setCountValues] = useState({})
  const [loading, setLoading] = useState(false)

  const loadSessions = async (sessionIdToOpen = null) => {
    const { data } = await client.get('/inventory/sessions')
    setSessions(data)
    const targetId = sessionIdToOpen ?? selectedSessionId ?? data[0]?.id
    if (targetId) {
      await loadSessionDetails(targetId)
    } else {
      setSelectedSession(null)
      setSelectedSessionId('')
    }
  }

  const loadSessionDetails = async (id) => {
    const { data } = await client.get(`/inventory/sessions/${id}`)
    setSelectedSession(data)
    setSelectedSessionId(String(id))
    const nextCountValues = {}
    data.items.forEach((item) => {
      nextCountValues[item.id] = {
        countedQuantity: item.countedQuantity ?? item.systemQuantity,
        notes: item.notes ?? '',
      }
    })
    setCountValues(nextCountValues)
  }

  useEffect(() => {
    loadSessions()
  }, [])

  const createSession = async (event) => {
    event.preventDefault()
    try {
      setError('')
      setMessage('')
      const { data } = await client.post('/inventory/sessions', createForm)
      setCreateForm(createFormInitial)
      setMessage('Sessione inventario creata correttamente')
      await loadSessions(data.id)
    } catch (err) {
      setError(err.response?.data?.message || 'Errore creazione inventario')
    }
  }

  const saveCount = async (itemId) => {
    try {
      setError('')
      const payload = countValues[itemId]
      await client.post('/inventory/counts', {
        itemId,
        countedQuantity: Number(payload.countedQuantity),
        notes: payload.notes,
      })
      await loadSessionDetails(selectedSessionId)
      setMessage('Conteggio salvato')
    } catch (err) {
      setError(err.response?.data?.message || 'Errore salvataggio conteggio')
    }
  }

  const completeSession = async () => {
    if (!selectedSessionId) return
    if (!window.confirm('Chiudere inventario? Se abilitato, verranno create le rettifiche automatiche.')) return
    try {
      setError('')
      await client.post(`/inventory/sessions/${selectedSessionId}/complete`)
      setMessage('Inventario chiuso correttamente')
      await loadSessions(selectedSessionId)
    } catch (err) {
      setError(err.response?.data?.message || 'Errore chiusura inventario')
    }
  }

  const summary = useMemo(() => {
    if (!selectedSession?.items?.length) return null
    const counted = selectedSession.items.filter((item) => item.countedQuantity !== null).length
    const diffs = selectedSession.items.filter((item) => item.difference && item.difference !== 0).length
    return { counted, diffs, total: selectedSession.items.length }
  }, [selectedSession])

  return (
    <div className="grid-2 inventory-grid">
      <section className="card">
        <h2>Nuovo inventario guidato</h2>
        <form className="stack-form" onSubmit={createSession}>
          <input
            placeholder="Nome sessione (es. Inventario aprile corsia A)"
            value={createForm.name}
            onChange={(e) => setCreateForm({ ...createForm, name: e.target.value })}
            required
          />
          <input
            placeholder="Filtro categoria (opzionale)"
            value={createForm.category}
            onChange={(e) => setCreateForm({ ...createForm, category: e.target.value })}
          />
          <input
            placeholder="Filtro fornitore (opzionale)"
            value={createForm.supplier}
            onChange={(e) => setCreateForm({ ...createForm, supplier: e.target.value })}
          />
          <input
            placeholder="Filtro posizione/scaffale (opzionale)"
            value={createForm.location}
            onChange={(e) => setCreateForm({ ...createForm, location: e.target.value })}
          />
          <textarea
            placeholder="Note sessione"
            value={createForm.notes}
            onChange={(e) => setCreateForm({ ...createForm, notes: e.target.value })}
          />
          <label className="check-line">
            <input
              type="checkbox"
              checked={createForm.applyAdjustmentsOnComplete}
              onChange={(e) => setCreateForm({ ...createForm, applyAdjustmentsOnComplete: e.target.checked })}
            />
            Applica rettifiche automatiche alla chiusura
          </label>
          <button type="submit">Crea sessione</button>
        </form>

        <div className="divider" />

        <h3>Sessioni inventario</h3>
        <div className="stack-gap">
          {sessions.map((session) => (
            <button
              key={session.id}
              type="button"
              className={`session-card ${String(session.id) === selectedSessionId ? 'active' : ''}`}
              onClick={() => loadSessionDetails(session.id)}
            >
              <strong>{session.name}</strong>
              <span>{session.status}</span>
              <small>{new Date(session.createdAt).toLocaleString('it-IT')}</small>
            </button>
          ))}
        </div>
      </section>

      <section className="card">
        <div className="section-head">
          <div>
            <h2>Dettaglio inventario</h2>
            {selectedSession && (
              <p className="muted-text">
                {selectedSession.name} · {selectedSession.status} · creato da {selectedSession.createdBy}
              </p>
            )}
          </div>
          {selectedSession && selectedSession.status === 'OPEN' && (
            <button type="button" onClick={completeSession}>Chiudi inventario</button>
          )}
        </div>

        {message && <div className="success-box">{message}</div>}
        {error && <div className="error-box">{error}</div>}

        {!selectedSession && <div className="muted-text">Seleziona o crea una sessione inventario.</div>}

        {selectedSession && (
          <>
            {summary && (
              <div className="stats-grid compact-stats">
                <div className="card stat-card">
                  <span>Righe</span>
                  <strong>{summary.total}</strong>
                </div>
                <div className="card stat-card">
                  <span>Contate</span>
                  <strong>{summary.counted}</strong>
                </div>
                <div className="card stat-card">
                  <span>Differenze</span>
                  <strong>{summary.diffs}</strong>
                </div>
                <div className="card stat-card">
                  <span>Auto rettifiche</span>
                  <strong>{selectedSession.applyAdjustmentsOnComplete ? 'Sì' : 'No'}</strong>
                </div>
              </div>
            )}

            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>SKU</th>
                    <th>Prodotto</th>
                    <th>Sistema</th>
                    <th>Contata</th>
                    <th>Differenza</th>
                    <th>Note</th>
                    <th>Azione</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedSession.items.map((item) => {
                    const formValue = countValues[item.id] || { countedQuantity: item.systemQuantity, notes: '' }
                    return (
                      <tr key={item.id}>
                        <td>{item.sku}</td>
                        <td>
                          <div>{item.productName}</div>
                          <small className="muted-text">{item.category} · {item.warehouseLocation || 'n/d'}</small>
                        </td>
                        <td>{item.systemQuantity}</td>
                        <td style={{ minWidth: 110 }}>
                          <input
                            type="number"
                            min="0"
                            value={formValue.countedQuantity}
                            onChange={(e) => setCountValues({
                              ...countValues,
                              [item.id]: { ...formValue, countedQuantity: e.target.value },
                            })}
                          />
                        </td>
                        <td className={item.difference === 0 ? 'diff-ok' : item.difference ? 'diff-bad' : ''}>
                          {item.difference ?? '-'}
                        </td>
                        <td style={{ minWidth: 220 }}>
                          <input
                            placeholder="Note conteggio"
                            value={formValue.notes}
                            onChange={(e) => setCountValues({
                              ...countValues,
                              [item.id]: { ...formValue, notes: e.target.value },
                            })}
                          />
                        </td>
                        <td>
                          <button
                            type="button"
                            className="secondary-btn"
                            disabled={loading || selectedSession.status !== 'OPEN'}
                            onClick={() => saveCount(item.id)}
                          >
                            Salva
                          </button>
                        </td>
                      </tr>
                    )
                  })}
                </tbody>
              </table>
            </div>
          </>
        )}
      </section>
    </div>
  )
}
