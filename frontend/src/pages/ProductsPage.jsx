import { useEffect, useState } from 'react'
import client from '../api/client'

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
  const [search, setSearch] = useState('')
  const [form, setForm] = useState(emptyForm)
  const [message, setMessage] = useState('')

  const loadProducts = async () => {
    const { data } = await client.get('/products', { params: search ? { search } : {} })
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

  return (
    <div className="grid-2">
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
          <h2>Catalogo prodotti</h2>
          <form className="inline-form" onSubmit={onSearch}>
            <input placeholder="Cerca per nome o SKU" value={search} onChange={(e) => setSearch(e.target.value)} />
            <button type="submit">Cerca</button>
          </form>
        </div>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>SKU</th>
                <th>Nome</th>
                <th>Categoria</th>
                <th>Disponibile</th>
                <th>Scorta minima</th>
              </tr>
            </thead>
            <tbody>
              {products.map((product) => (
                <tr key={product.id}>
                  <td>{product.sku}</td>
                  <td>{product.name}</td>
                  <td>{product.category}</td>
                  <td>{product.quantityAvailable} {product.unitOfMeasure}</td>
                  <td>{product.minimumThreshold}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}
