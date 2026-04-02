import { Link, NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function AppLayout() {
  const { user, logout } = useAuth()

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <Link to="/" className="brand">WarehouseFlow</Link>
        <nav className="nav-links">
          <NavLink to="/">Dashboard</NavLink>
          <NavLink to="/products">Prodotti</NavLink>
          <NavLink to="/movements">Movimenti</NavLink>
          <NavLink to="/orders">Ordini</NavLink>
          <NavLink to="/inventory-history">Storico inventari</NavLink>
          <NavLink to="/audit-logs">Audit log</NavLink>
          <NavLink to="/users">Utenti</NavLink>
        </nav>
      </aside>
      <main className="content">
        <header className="topbar">
          <div>
            <strong>{user?.firstName} {user?.lastName}</strong>
            <span className="role-pill">{user?.role}</span>
          </div>
          <button className="secondary-btn" onClick={logout}>Esci</button>
        </header>
        <Outlet />
      </main>
    </div>
  )
}
