import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import client from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const bootstrap = async () => {
      const token = localStorage.getItem('warehouseflow_token')
      if (!token) {
        setLoading(false)
        return
      }

      try {
        const { data } = await client.get('/users/me')
        setUser(data)
      } catch (error) {
        localStorage.removeItem('warehouseflow_token')
        setUser(null)
      } finally {
        setLoading(false)
      }
    }

    bootstrap()
  }, [])

  const login = async (email, password) => {
    const { data } = await client.post('/auth/login', { email, password })
    localStorage.setItem('warehouseflow_token', data.token)
    setUser(data.user)
  }

  const logout = () => {
    localStorage.removeItem('warehouseflow_token')
    setUser(null)
  }

  const value = useMemo(() => ({ user, login, logout, loading }), [user, loading])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}
