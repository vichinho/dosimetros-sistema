import { Navigate } from 'react-router-dom'
import { useAuth } from './AuthContext'

// Protege rutas: exige sesión y, opcionalmente, uno de los roles indicados.
export default function ProtectedRoute({ children, roles }) {
  const { isAuthenticated, rol } = useAuth()

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  if (roles && !roles.includes(rol)) {
    return <Navigate to="/" replace />
  }

  return children
}
