import { useEffect, useState } from 'react'
import client from '../api/client'
import { downloadFile } from '../utils/download'

const emptyForm = {
  sku: '',
  name: '',
  description: '',
  category: '',
  unitOfMeasure: 'pz',
  minimumThreshold: 0,
  supplier: '',
  warehouseLocation: '',
  active: true,
}

export default function ProductsPage() {
  const [products, setProducts] = useState([])
  const [filters, setFilters] = useState({ search: '', category: '', supplier: '', lowStockOnly: false })
  const [form, setForm] = useState(emptyForm)
  const [message, setMessage] = useState('')

  const loadProducts = async () => {
    const params = { ...filters }
    if (!params.search) delete params.search
    if (!params.category) delete params.category
    if (!params.supplier) delete params.supplier
    if (!params.lowStockOnly) delete params.lowStockOnly
    const { data } = await client.get('/products', { params })
    setProducts(data)
  }

  useEffect(() => {
    loadProducts()
  }, [])

  const submit = async (event) => {
    event.preventDefault()
    await client.post('/products', { ...form, minimumThreshold: Number(form.minimumThreshold) })
    setForm(emptyForm)
    setMessage('Prodotto creato correttamente')
    await loadProducts()
  }

  const onSearch = async (event) => {
    event.preventDefault()
    await loadProducts()
  }

  const exportParams = {
    ...filters,
    lowStockOnly: filters.lowStockOnly || undefined,
  }

  return (
    <div className="stack-gap">
      <section className="grid-2">
        <section className="card">
          <h2>Nuovo prodotto</h2>
          <form className="stack-form" onSubmit={submit}>
            <input placeholder="SKU" value={form.sku} onChange={(e) => setForm({ ...form, sku: e.target.value })} required />
            <input placeholder="Nome" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
            <textarea placeholder="Descrizione" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
            <input placeholder="Categoria" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} required />
            <input placeholder="Unità di misura" value={form.unitOfMeasure} onChange={(e) => setForm({ ...form, unitOfMeasure: e.target.value })} required />
            <input type="number" placeholder="Scorta minima" value={form.minimumThreshold} onChange={(e) => setForm({ ...form, minimumThreshold: e.target.value })} required />
            <input placeholder="Fornitore" value={form.supplier} onChange={(e) => setForm({ ...form, supplier: e.target.value })} />
            <input placeholder="Posizione magazzino" value={form.warehouseLocation} onChange={(e) => setForm({ ...form, warehouseLocation: e.target.value })} />
            <button type="submit">Salva prodotto</button>
            {message && <div className="success-box">{message}</div>}
          </form>
        </section>

        <section className="card">
          <div className="section-head">
            <h2>Filtri prodotti</h2>
            <div className="inline-form compact-actions wrap">
              <button type="button" onClick={() => downloadFile('/exports/products.xlsx', 'prodotti.xlsx', exportParams)}>Excel</button>
              <button type="button" onClick={() => downloadFile('/exports/products.pdf', 'prodotti.pdf', exportParams)}>PDF</button>
            </div>
          </div>
          <form className="stack-form" onSubmit={onSearch}>
            <input placeholder="Cerca per nome o SKU" value={filters.search} onChange={(e) => setFilters({ ...filters, search: e.target.value })} />
            <input placeholder="Categoria" value={filters.category} onChange={(e) => setFilters({ ...filters, category: e.target.value })} />
            <input placeholder="Fornitore" value={filters.supplier} onChange={(e) => setFilters({ ...filters, supplier: e.target.value })} />
            <label className="checkbox-line">
              <input type="checkbox" checked={filters.lowStockOnly} onChange={(e) => setFilters({ ...filters, lowStockOnly: e.target.checked })} />
              Solo prodotti sotto scorta
            </label>
            <button type="submit">Applica filtri</button>
          </form>
        </section>
      </section>

      <section className="card">
        <h2>Catalogo prodotti</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>SKU</th>
                <th>Nome</th>
                <th>Categoria</th>
                <th>Fornitore</th>
                <th>Disponibile</th>
                <th>Scorta minima</th>
                <th>Posizione</th>
              </tr>
            </thead>
            <tbody>
              {products.map((product) => (
                <tr key={product.id}>
                  <td>{product.sku}</td>
                  <td>{product.name}</td>
                  <td>{product.category}</td>
                  <td>{product.supplier}</td>
                  <td>{product.quantityAvailable} {product.unitOfMeasure}</td>
                  <td>{product.minimumThreshold}</td>
                  <td>{product.warehouseLocation}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}
