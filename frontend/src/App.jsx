import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './auth/AuthContext'
import ProtectedRoute from './auth/ProtectedRoute'
import Layout from './components/Layout'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Stock from './pages/Stock'
import Asignar from './pages/Asignar'
import Armar from './pages/Armar'
import Buscar from './pages/Buscar'
import Clientes from './pages/Clientes'
import Ejecutivos from './pages/Ejecutivos'
import Duplicados from './pages/Duplicados'
import TiposPorta from './pages/TiposPorta'
import Importar from './pages/Importar'
import Usuarios from './pages/Usuarios'
import MisDosimetros from './pages/MisDosimetros'
import MisAsignaciones from './pages/MisAsignaciones'
import MisClientes from './pages/MisClientes'

// Redirige a la pantalla inicial según el rol.
function Home() {
  const { rol } = useAuth()
  if (rol === 'EJECUTIVO') return <Navigate to="/mis-dosimetros" replace />
  if (rol === 'OPERADOR') return <Navigate to="/stock" replace />
  return <Dashboard />
}

const OPER = ['ADMIN', 'OPERADOR']

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route path="/" element={<Home />} />
        <Route path="/stock" element={<ProtectedRoute roles={OPER}><Stock /></ProtectedRoute>} />
        <Route path="/armar" element={<ProtectedRoute roles={OPER}><Armar /></ProtectedRoute>} />
        <Route path="/asignar" element={<ProtectedRoute roles={OPER}><Asignar /></ProtectedRoute>} />
        <Route path="/buscar" element={<ProtectedRoute roles={OPER}><Buscar /></ProtectedRoute>} />
        <Route path="/clientes" element={<ProtectedRoute roles={OPER}><Clientes /></ProtectedRoute>} />
        <Route path="/ejecutivos" element={<ProtectedRoute roles={OPER}><Ejecutivos /></ProtectedRoute>} />
        <Route path="/duplicados" element={<ProtectedRoute roles={OPER}><Duplicados /></ProtectedRoute>} />
        <Route path="/tipos-porta" element={<ProtectedRoute roles={OPER}><TiposPorta /></ProtectedRoute>} />
        <Route path="/importar" element={<ProtectedRoute roles={['ADMIN']}><Importar /></ProtectedRoute>} />
        <Route path="/usuarios" element={<ProtectedRoute roles={['ADMIN']}><Usuarios /></ProtectedRoute>} />
        <Route
          path="/mis-dosimetros"
          element={<ProtectedRoute roles={['EJECUTIVO']}><MisDosimetros /></ProtectedRoute>}
        />
        <Route
          path="/mis-asignaciones"
          element={<ProtectedRoute roles={['EJECUTIVO']}><MisAsignaciones /></ProtectedRoute>}
        />
        <Route
          path="/mis-clientes"
          element={<ProtectedRoute roles={['EJECUTIVO']}><MisClientes /></ProtectedRoute>}
        />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
