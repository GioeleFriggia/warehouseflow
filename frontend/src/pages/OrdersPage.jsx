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
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [receiveState, setReceiveState] = useState({})
  const [uploadingOrderId, setUploadingOrderId] = useState(null)

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
    try {
      setError('')
      await client.post('/orders', { supplier, notes, items: items.map(({ productId, quantity }) => ({ productId, quantity })) })
      setSupplier('')
      setNotes('')
      setItems([])
      setMessage('Ordine creato correttamente')
      await loadData()
    } catch (err) {
      setError(err.response?.data?.message || 'Errore creazione ordine')
    }
  }

  const updateStatus = async (id, status) => {
    try {
      setError('')
      await client.patch(`/orders/${id}/status`, { status })
      setMessage('Stato ordine aggiornato')
      await loadData()
    } catch (err) {
      setError(err.response?.data?.message || 'Errore aggiornamento stato')
    }
  }

  const openReceive = (order) => {
    const initialItems = {}
    order.items.forEach((item) => {
      initialItems[item.productId] = {
        quantityReceived: item.quantity,
        destinationLocation: '',
      }
    })
    setReceiveState({
      orderId: order.id,
      notes: '',
      items: initialItems,
    })
  }

  const receiveOrder = async () => {
    try {
      setError('')
      const payload = {
        notes: receiveState.notes,
        items: Object.entries(receiveState.items).map(([productId, value]) => ({
          productId: Number(productId),
          quantityReceived: Number(value.quantityReceived || 0),
          destinationLocation: value.destinationLocation || '',
        })),
      }
      await client.post(`/orders/${receiveState.orderId}/receive`, payload)
      setReceiveState({})
      setMessage('Ordine ricevuto e carichi creati automaticamente')
      await loadData()
    } catch (err) {
      setError(err.response?.data?.message || 'Errore ricevimento ordine')
    }
  }

  const uploadDocument = async (orderId, file) => {
    if (!file) return
    try {
      setUploadingOrderId(orderId)
      setError('')
      const formData = new FormData()
      formData.append('file', file)
      await client.post(`/orders/${orderId}/documents`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      setMessage('Documento caricato correttamente')
      await loadData()
    } catch (err) {
      setError(err.response?.data?.message || 'Errore caricamento documento')
    } finally {
      setUploadingOrderId(null)
    }
  }

  const downloadDocument = async (documentId, fileName) => {
    const response = await client.get(`/orders/documents/${documentId}/download`, {
      responseType: 'blob',
    })
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', fileName)
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  }

  return (
    <div className="grid-2">
      <section className="card">
        <h2>Nuovo ordine</h2>
        {message && <div className="success-box">{message}</div>}
        {error && <div className="error-box">{error}</div>}

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
        <div className="orders-stack">
          {orders.map((order) => (
            <div key={order.id} className="order-box">
              <div className="section-head">
                <div>
                  <strong>Ordine #{order.id}</strong>
                  <div className="muted-text">{order.supplier} · {order.status} · {order.createdBy}</div>
                </div>
                <div className="inline-form compact-actions">
                  <button type="button" onClick={() => updateStatus(order.id, 'SUBMITTED')}>Invia</button>
                  <button type="button" onClick={() => openReceive(order)}>Ricevi merce</button>
                </div>
              </div>

              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>SKU</th>
                      <th>Prodotto</th>
                      <th>Quantità</th>
                    </tr>
                  </thead>
                  <tbody>
                    {order.items.map((item) => (
                      <tr key={item.id}>
                        <td>{item.sku}</td>
                        <td>{item.productName}</td>
                        <td>{item.quantity}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              <div className="documents-box">
                <div className="section-head">
                  <strong>Documenti allegati</strong>
                  <label className="upload-btn">
                    <input
                      type="file"
                      hidden
                      onChange={(e) => uploadDocument(order.id, e.target.files?.[0])}
                    />
                    {uploadingOrderId === order.id ? 'Caricamento...' : 'Carica documento'}
                  </label>
                </div>

                {order.documents?.length ? (
                  <ul className="list-box">
                    {order.documents.map((doc) => (
                      <li key={doc.id}>
                        <button type="button" className="link-btn" onClick={() => downloadDocument(doc.id, doc.fileName)}>
                          {doc.fileName}
                        </button>
                        <span className="muted-text"> · {doc.uploadedBy}</span>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <div className="muted-text">Nessun documento allegato</div>
                )}
              </div>
            </div>
          ))}
        </div>

        {receiveState.orderId && (
          <div className="receive-box">
            <div className="section-head">
              <strong>Ricevimento ordine #{receiveState.orderId}</strong>
              <button type="button" className="secondary-btn" onClick={() => setReceiveState({})}>Chiudi</button>
            </div>

            <div className="stack-gap">
              {Object.entries(receiveState.items).map(([productId, value]) => {
                const product = products.find((item) => item.id === Number(productId))
                return (
                  <div key={productId} className="receive-row">
                    <div>
                      <strong>{product?.sku} - {product?.name}</strong>
                    </div>
                    <input
                      type="number"
                      min="0"
                      value={value.quantityReceived}
                      onChange={(e) => setReceiveState({
                        ...receiveState,
                        items: {
                          ...receiveState.items,
                          [productId]: { ...value, quantityReceived: e.target.value },
                        },
                      })}
                    />
                    <input
                      placeholder="Destinazione"
                      value={value.destinationLocation}
                      onChange={(e) => setReceiveState({
                        ...receiveState,
                        items: {
                          ...receiveState.items,
                          [productId]: { ...value, destinationLocation: e.target.value },
                        },
                      })}
                    />
                  </div>
                )
              })}
            </div>

            <textarea
              placeholder="Note ricevimento"
              value={receiveState.notes}
              onChange={(e) => setReceiveState({ ...receiveState, notes: e.target.value })}
            />

            <button type="button" onClick={receiveOrder}>Conferma ricevimento e crea carichi</button>
          </div>
        )}
      </section>
    </div>
  )
}
