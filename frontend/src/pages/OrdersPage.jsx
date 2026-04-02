import { useEffect, useState } from 'react'
import client from '../api/client'

export default function OrdersPage() {
  const [products, setProducts] = useState([])
  const [orders, setOrders] = useState([])
  const [supplier, setSupplier] = useState('')
  const [notes, setNotes] = useState('')
  const [productId, setProductId] = useState('')
  const [quantity, setQuantity] = useState(1)
  const [items, setItems] = useState([])

  const loadData = async () => {
    const [productsRes, ordersRes] = await Promise.all([
      client.get('/products'),
      client.get('/orders'),
    ])
    setProducts(productsRes.data)
    setOrders(ordersRes.data)
  }

  useEffect(() => {
    loadData()
  }, [])

  const addItem = () => {
    if (!productId) return
    const product = products.find((item) => item.id === Number(productId))
    setItems([...items, { productId: Number(productId), quantity: Number(quantity), label: `${product.sku} - ${product.name}` }])
    setProductId('')
    setQuantity(1)
  }

  const submit = async (event) => {
    event.preventDefault()
    await client.post('/orders', { supplier, notes, items: items.map(({ productId, quantity }) => ({ productId, quantity })) })
    setSupplier('')
    setNotes('')
    setItems([])
    await loadData()
  }

  const updateStatus = async (id, status) => {
    await client.patch(`/orders/${id}/status`, { status })
    await loadData()
  }

  return (
    <div className="grid-2">
      <section className="card">
        <h2>Nuovo ordine</h2>
        <form className="stack-form" onSubmit={submit}>
          <input placeholder="Fornitore" value={supplier} onChange={(e) => setSupplier(e.target.value)} required />
          <textarea placeholder="Note" value={notes} onChange={(e) => setNotes(e.target.value)} />
          <div className="inline-form">
            <select value={productId} onChange={(e) => setProductId(e.target.value)}>
              <option value="">Seleziona prodotto</option>
              {products.map((product) => <option key={product.id} value={product.id}>{product.sku} - {product.name}</option>)}
            </select>
            <input type="number" min="1" value={quantity} onChange={(e) => setQuantity(e.target.value)} />
            <button type="button" onClick={addItem}>Aggiungi riga</button>
          </div>
          <ul className="list-box">
            {items.map((item, index) => <li key={index}>{item.label} - {item.quantity}</li>)}
          </ul>
          <button type="submit" disabled={items.length === 0}>Salva ordine</button>
        </form>
      </section>

      <section className="card">
        <h2>Ordini</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Fornitore</th>
                <th>Stato</th>
                <th>Creato da</th>
                <th>Azione</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id}>
                  <td>{order.id}</td>
                  <td>{order.supplier}</td>
                  <td>{order.status}</td>
                  <td>{order.createdBy}</td>
                  <td>
                    <div className="inline-form compact-actions">
                      <button type="button" onClick={() => updateStatus(order.id, 'SUBMITTED')}>Invia</button>
                      <button type="button" onClick={() => updateStatus(order.id, 'RECEIVED')}>Ricevuto</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}
