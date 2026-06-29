import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

// Define qué ítems del menú ve cada rol.
const NAV_ITEMS = [
  { to: '/', label: 'Dashboard', roles: ['ADMIN'] },
  { to: '/stock', label: 'Stock', roles: ['ADMIN', 'OPERADOR'] },
  { to: '/asignar', label: 'Asignar', roles: ['ADMIN', 'OPERADOR'] },
  { to: '/buscar', label: 'Buscar dosímetro', roles: ['ADMIN', 'OPERADOR'] },
  { to: '/clientes', label: 'Clientes', roles: ['ADMIN', 'OPERADOR'] },
  { to: '/ejecutivos', label: 'Ejecutivos', roles: ['ADMIN', 'OPERADOR'] },
  { to: '/duplicados', label: 'Duplicados', roles: ['ADMIN', 'OPERADOR'] },
  { to: '/importar', label: 'Importar Excel', roles: ['ADMIN'] },
  { to: '/mis-dosimetros', label: 'Mis dosímetros', roles: ['EJECUTIVO'] },
  { to: '/mis-clientes', label: 'Mis clientes', roles: ['EJECUTIVO'] },
]

export default function Layout() {
  const { username, rol, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const items = NAV_ITEMS.filter((item) => item.roles.includes(rol))

  return (
    <div className="min-h-full flex">
      <aside className="w-60 bg-slate-900 text-slate-200 flex flex-col">
        <div className="px-5 py-5 border-b border-slate-700">
          <h1 className="text-lg font-bold text-white">Dosímetros</h1>
          <p className="text-xs text-slate-400 mt-1">Gestión de stock</p>
        </div>
        <nav className="flex-1 px-3 py-4 space-y-1">
          {items.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              className={({ isActive }) =>
                `block px-3 py-2 rounded-lg text-sm font-medium transition ${
                  isActive ? 'bg-blue-600 text-white' : 'text-slate-300 hover:bg-slate-800'
                }`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="px-5 py-4 border-t border-slate-700">
          <p className="text-sm text-white font-medium">{username}</p>
          <p className="text-xs text-slate-400 mb-3">{rol}</p>
          <button
            onClick={handleLogout}
            className="w-full text-sm bg-slate-800 hover:bg-slate-700 text-slate-200 py-2 rounded-lg transition"
          >
            Cerrar sesión
          </button>
        </div>
      </aside>

      <main className="flex-1 p-8 overflow-auto">
        <Outlet />
      </main>
    </div>
  )
}
