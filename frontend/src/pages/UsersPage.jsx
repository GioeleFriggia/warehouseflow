import { useEffect, useState } from 'react'
import client from '../api/client'

const emptyForm = {
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  role: 'WAREHOUSE',
  active: true,
}

export default function UsersPage() {
  const [users, setUsers] = useState([])
  const [form, setForm] = useState(emptyForm)

  const loadUsers = async () => {
    const { data } = await client.get('/users')
    setUsers(data)
  }

  useEffect(() => {
    loadUsers()
  }, [])

  const submit = async (event) => {
    event.preventDefault()
    await client.post('/users', form)
    setForm(emptyForm)
    await loadUsers()
  }

  return (
    <div className="grid-2">
      <section className="card">
        <h2>Nuovo utente</h2>
        <form className="stack-form" onSubmit={submit}>
          <input placeholder="Nome" value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} required />
          <input placeholder="Cognome" value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} required />
          <input type="email" placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
          <input type="password" placeholder="Password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required />
          <select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
            <option value="WAREHOUSE">Magazziniere</option>
            <option value="STORE_OPERATOR">Operatore negozio</option>
            <option value="MANAGER">Manager</option>
            <option value="ADMIN">Admin</option>
          </select>
          <button type="submit">Crea utente</button>
        </form>
      </section>

      <section className="card">
        <h2>Utenti</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Nome</th>
                <th>Email</th>
                <th>Ruolo</th>
                <th>Attivo</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id}>
                  <td>{user.firstName} {user.lastName}</td>
                  <td>{user.email}</td>
                  <td>{user.role}</td>
                  <td>{user.active ? 'Sì' : 'No'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}
