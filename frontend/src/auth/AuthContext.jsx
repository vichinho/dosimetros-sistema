import { createContext, useContext, useState } from 'react'
import { login as loginRequest } from '../api/endpoints'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => ({
    token: localStorage.getItem('token'),
    username: localStorage.getItem('username'),
    rol: localStorage.getItem('rol'),
  }))

  const login = async (username, password) => {
    const data = await loginRequest(username, password)
    localStorage.setItem('token', data.token)
    localStorage.setItem('username', data.username)
    localStorage.setItem('rol', data.rol)
    setAuth({ token: data.token, username: data.username, rol: data.rol })
    return data
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('rol')
    setAuth({ token: null, username: null, rol: null })
  }

  const value = {
    ...auth,
    isAuthenticated: Boolean(auth.token),
    login,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth debe usarse dentro de AuthProvider')
  return ctx
}
