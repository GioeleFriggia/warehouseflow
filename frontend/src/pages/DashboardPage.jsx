import { useEffect, useState } from 'react'
import client from '../api/client'

export default function DashboardPage() {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    const load = async () => {
      try {
        const response = await client.get('/dashboard')
        setData(response.data)
      } catch (err) {
        setError(err.response?.data?.message || 'Errore caricamento dashboard')
      }
    }
    load()
  }, [])

  if (error) return <div className="error-box">{error}</div>
  if (!data) return <div className="card">Caricamento dashboard...</div>

  return (
    <div className="stack-gap">
      <section className="stats-grid">
        <div className="card stat-card"><span>Prodotti</span><strong>{data.totalProducts}</strong></div>
        <div className="card stat-card"><span>Sotto scorta</span><strong>{data.lowStockProducts}</strong></div>
        <div className="card stat-card"><span>Movimenti oggi</span><strong>{data.movementsToday}</strong></div>
        <div className="card stat-card"><span>Ordini aperti</span><strong>{data.openOrders}</strong></div>
      </section>

      <section className="card">
        <h2>Avvisi automatici sotto scorta</h2>
        {data.lowStockAlerts?.length ? (
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
                  <th>Mancano</th>
                </tr>
              </thead>
              <tbody>
                {data.lowStockAlerts.map((item) => (
                  <tr key={item.productId}>
                    <td>{item.sku}</td>
                    <td>{item.productName}</td>
                    <td>{item.category}</td>
                    <td>{item.supplier}</td>
                    <td>{item.quantityAvailable}</td>
                    <td>{item.minimumThreshold}</td>
                    <td>{item.missingQuantity}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : <div className="success-box">Nessun prodotto sotto scorta.</div>}
      </section>

      <section className="card">
        <h2>Movimenti recenti</h2>
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
              {data.recentMovements.map((movement) => (
                <tr key={movement.id}>
                  <td>{new Date(movement.createdAt).toLocaleString('it-IT')}</td>
                  <td>{movement.productName}</td>
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
