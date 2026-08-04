import { createContext, useContext, useEffect, useRef, useState } from 'react'
import { login as loginRequest } from '../api/endpoints'

const AuthContext = createContext(null)

// Cierre de sesión automático tras este tiempo sin actividad del usuario.
const INACTIVIDAD_MS = 30 * 60 * 1000 // 30 minutos

export function AuthProvider({ children }) {
  // La sesión se guarda en sessionStorage: se borra al cerrar el navegador
  // (a diferencia de localStorage, que persistía indefinidamente).
  const [auth, setAuth] = useState(() => ({
    token: sessionStorage.getItem('token'),
    username: sessionStorage.getItem('username'),
    rol: sessionStorage.getItem('rol'),
  }))
  const timerRef = useRef(null)

  const login = async (username, password) => {
    const data = await loginRequest(username, password)
    sessionStorage.setItem('token', data.token)
    sessionStorage.setItem('username', data.username)
    sessionStorage.setItem('rol', data.rol)
    setAuth({ token: data.token, username: data.username, rol: data.rol })
    return data
  }

  const logout = () => {
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('username')
    sessionStorage.removeItem('rol')
    setAuth({ token: null, username: null, rol: null })
  }

  // Cierra la sesión tras un periodo de inactividad. El temporizador se
  // reinicia con cualquier interacción (mouse, teclado, scroll, touch).
  useEffect(() => {
    if (!auth.token) return
    const reiniciar = () => {
      if (timerRef.current) clearTimeout(timerRef.current)
      timerRef.current = setTimeout(logout, INACTIVIDAD_MS)
    }
    const eventos = ['mousemove', 'mousedown', 'keydown', 'scroll', 'touchstart', 'click']
    eventos.forEach((e) => window.addEventListener(e, reiniciar, { passive: true }))
    reiniciar() // arranca el conteo
    return () => {
      eventos.forEach((e) => window.removeEventListener(e, reiniciar))
      if (timerRef.current) clearTimeout(timerRef.current)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [auth.token])

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
