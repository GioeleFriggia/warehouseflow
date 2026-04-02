import { useEffect, useState } from 'react'
import client from '../api/client'
import { downloadFile } from '../utils/download'

const initialForm = {
  productId: '',
  movementType: 'INBOUND',
  quantity: 1,
  sourceLocation: '',
  destinationLocation: '',
  notes: '',
}

const initialFilters = {
  productId: '',
  movementType: '',
  performedBy: '',
  dateFrom: '',
  dateTo: '',
}

export default function MovementsPage() {
  const [products, setProducts] = useState([])
  const [movements, setMovements] = useState([])
  const [form, setForm] = useState(initialForm)
  const [filters, setFilters] = useState(initialFilters)
  const [message, setMessage] = useState('')

  const loadProducts = async () => {
    const { data } = await client.get('/products')
    setProducts(data)
  }

  const loadMovements = async () => {
    const params = { ...filters }
    Object.keys(params).forEach((key) => { if (!params[key]) delete params[key] })
    const { data } = await client.get('/stock/movements', { params })
    setMovements(data)
  }

  const loadData = async () => {
    await Promise.all([loadProducts(), loadMovements()])
  }

  useEffect(() => {
    loadData()
  }, [])

  const submit = async (event) => {
    event.preventDefault()
    await client.post('/stock/movements', {
      ...form,
      productId: Number(form.productId),
      quantity: Number(form.quantity),
    })
    setForm(initialForm)
    setMessage('Movimento registrato correttamente')
    await loadData()
  }

  const applyFilters = async (event) => {
    event.preventDefault()
    await loadMovements()
  }

  return (
    <div className="stack-gap">
      <section className="grid-2">
        <section className="card">
          <h2>Nuovo movimento</h2>
          <form className="stack-form" onSubmit={submit}>
            <select value={form.productId} onChange={(e) => setForm({ ...form, productId: e.target.value })} required>
              <option value="">Seleziona prodotto</option>
              {products.map((product) => <option key={product.id} value={product.id}>{product.sku} - {product.name}</option>)}
            </select>
            <select value={form.movementType} onChange={(e) => setForm({ ...form, movementType: e.target.value })}>
              <option value="INBOUND">Carico</option>
              <option value="OUTBOUND">Scarico</option>
              <option value="ADJUSTMENT">Rettifica quantità</option>
            </select>
            <input type="number" min="1" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} required />
            <input placeholder="Origine" value={form.sourceLocation} onChange={(e) => setForm({ ...form, sourceLocation: e.target.value })} />
            <input placeholder="Destinazione" value={form.destinationLocation} onChange={(e) => setForm({ ...form, destinationLocation: e.target.value })} />
            <textarea placeholder="Note" value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} />
            <button type="submit">Registra movimento</button>
            {message && <div className="success-box">{message}</div>}
          </form>
        </section>

        <section className="card">
          <div className="section-head">
            <h2>Filtri movimenti</h2>
            <div className="inline-form compact-actions wrap">
              <button type="button" onClick={() => downloadFile('/exports/movements.xlsx', 'movimenti.xlsx', filters)}>Excel</button>
              <button type="button" onClick={() => downloadFile('/exports/movements.pdf', 'movimenti.pdf', filters)}>PDF</button>
            </div>
          </div>
          <form className="stack-form" onSubmit={applyFilters}>
            <select value={filters.productId} onChange={(e) => setFilters({ ...filters, productId: e.target.value })}>
              <option value="">Tutti i prodotti</option>
              {products.map((product) => <option key={product.id} value={product.id}>{product.sku} - {product.name}</option>)}
            </select>
            <select value={filters.movementType} onChange={(e) => setFilters({ ...filters, movementType: e.target.value })}>
              <option value="">Tutti i tipi</option>
              <option value="INBOUND">Carico</option>
              <option value="OUTBOUND">Scarico</option>
              <option value="ADJUSTMENT">Rettifica</option>
            </select>
            <input placeholder="Operatore" value={filters.performedBy} onChange={(e) => setFilters({ ...filters, performedBy: e.target.value })} />
            <div className="inline-form">
              <input type="date" value={filters.dateFrom} onChange={(e) => setFilters({ ...filters, dateFrom: e.target.value })} />
              <input type="date" value={filters.dateTo} onChange={(e) => setFilters({ ...filters, dateTo: e.target.value })} />
            </div>
            <button type="submit">Applica filtri</button>
          </form>
        </section>
      </section>

      <section className="card">
        <h2>Storico movimenti</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Data</th>
                <th>Prodotto</th>
                <th>Tipo</th>
                <th>Quantità</th>
                <th>Operatore</th>
                <th>Origine</th>
                <th>Destinazione</th>
              </tr>
            </thead>
            <tbody>
              {movements.map((movement) => (
                <tr key={movement.id}>
                  <td>{new Date(movement.createdAt).toLocaleString('it-IT')}</td>
                  <td>{movement.sku} - {movement.productName}</td>
                  <td>{movement.movementType}</td>
                  <td>{movement.quantity}</td>
                  <td>{movement.performedBy}</td>
                  <td>{movement.sourceLocation}</td>
                  <td>{movement.destinationLocation}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}
