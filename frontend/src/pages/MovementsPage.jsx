import { useEffect, useState } from 'react'
import client from '../api/client'

const initialForm = {
  productId: '',
  movementType: 'INBOUND',
  quantity: 1,
  sourceLocation: '',
  destinationLocation: '',
  notes: '',
}

export default function MovementsPage() {
  const [products, setProducts] = useState([])
  const [movements, setMovements] = useState([])
  const [form, setForm] = useState(initialForm)
  const [message, setMessage] = useState('')

  const loadData = async () => {
    const [productsRes, movementsRes] = await Promise.all([
      client.get('/products'),
      client.get('/stock/movements'),
    ])
    setProducts(productsRes.data)
    setMovements(movementsRes.data)
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

  return (
    <div className="grid-2">
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
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}
