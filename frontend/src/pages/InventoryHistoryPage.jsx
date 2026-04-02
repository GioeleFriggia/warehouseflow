import { useEffect, useState } from 'react'
import client from '../api/client'

export default function InventoryHistoryPage() {
  const [snapshots, setSnapshots] = useState([])
  const [form, setForm] = useState({ name: '', notes: '' })
  const [message, setMessage] = useState('')

  const load = async () => {
    const { data } = await client.get('/inventory/snapshots')
    setSnapshots(data)
  }

  useEffect(() => {
    load()
  }, [])

  const createSnapshot = async (event) => {
    event.preventDefault()
    await client.post('/inventory/snapshots', form)
    setForm({ name: '', notes: '' })
    setMessage('Snapshot inventario creato correttamente')
    await load()
  }

  return (
    <div className="stack-gap">
      <section className="card">
        <h2>Nuovo snapshot inventario</h2>
        <form className="stack-form" onSubmit={createSnapshot}>
          <input placeholder="Nome snapshot" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          <textarea placeholder="Note" value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} />
          <button type="submit">Crea snapshot</button>
          {message && <div className="success-box">{message}</div>}
        </form>
      </section>

      {snapshots.map((snapshot) => (
        <section className="card" key={snapshot.id}>
          <div className="section-head">
            <div>
              <h2>{snapshot.name}</h2>
              <div className="muted">{new Date(snapshot.createdAt).toLocaleString('it-IT')} · {snapshot.createdBy}</div>
              {snapshot.notes ? <div className="muted">{snapshot.notes}</div> : null}
            </div>
            <div className="role-pill">{snapshot.items.length} righe</div>
          </div>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>SKU</th>
                  <th>Prodotto</th>
                  <th>Categoria</th>
                  <th>Fornitore</th>
                  <th>Disponibile</th>
                  <th>Minimo</th>
                  <th>Stato</th>
                </tr>
              </thead>
              <tbody>
                {snapshot.items.map((item) => (
                  <tr key={item.id}>
                    <td>{item.sku}</td>
                    <td>{item.productName}</td>
                    <td>{item.category}</td>
                    <td>{item.supplier}</td>
                    <td>{item.quantityAvailable}</td>
                    <td>{item.minimumThreshold}</td>
                    <td>{item.lowStock ? 'Sotto scorta' : 'OK'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      ))}
    </div>
  )
}
