import { useEffect, useMemo, useState } from 'react'
import { getMisClientes } from '../api/endpoints'
import { Card, Input, Alert, Badge, Loading, EmptyState, Pagination } from '../components/ui'

const POR_PAGINA = 25

export default function MisClientes() {
  const [clientes, setClientes] = useState([])
  const [busqueda, setBusqueda] = useState('')
  const [soloPendientes, setSoloPendientes] = useState(false)
  const [page, setPage] = useState(1)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    getMisClientes()
      .then(setClientes)
      .catch((err) =>
        setError(
          err.response?.status === 409
            ? 'Tu usuario no tiene un ejecutivo asociado. Contacta a un administrador.'
            : 'No se pudieron cargar tus clientes'
        )
      )
      .finally(() => setLoading(false))
  }, [])

  const pendientes = clientes.filter((c) => c.pendienteAsignacion)

  const filtrados = useMemo(() => {
    const q = busqueda.trim().toLowerCase()
    return clientes.filter((c) => {
      if (soloPendientes && !c.pendienteAsignacion) return false
      if (!q) return true
      return (
        c.razonSocial.toLowerCase().includes(q) ||
        (c.nombreCorto || '').toLowerCase().includes(q)
      )
    })
  }, [clientes, busqueda, soloPendientes])

  useEffect(() => { setPage(1) }, [busqueda, soloPendientes])

  const totalPages = Math.ceil(filtrados.length / POR_PAGINA)
  const visibles = filtrados.slice((page - 1) * POR_PAGINA, page * POR_PAGINA)

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-ink">Mis clientes</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          {pendientes.length} pendiente(s) de asignación de {clientes.length} cliente(s)
        </p>
      </div>

      <Alert type="error">{error}</Alert>

      <Card title="Filtros">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 items-end">
          <Input
            label="Buscar cliente"
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
            placeholder="Razón social o nombre fantasía…"
          />
          <label className="flex items-center gap-2 text-sm text-ink/70 pb-2">
            <input
              type="checkbox"
              checked={soloPendientes}
              onChange={(e) => setSoloPendientes(e.target.checked)}
            />
            Solo pendientes de asignación
          </label>
        </div>
      </Card>

      <Card title={`Clientes (${filtrados.length})`}>
        {loading ? (
          <Loading />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-slate-500 border-b border-slate-200">
                  <th className="py-2 font-medium">Razón social</th>
                  <th className="py-2 font-medium">Nombre fantasía</th>
                  <th className="py-2 font-medium">Estado</th>
                </tr>
              </thead>
              <tbody>
                {visibles.map((c) => (
                  <tr key={c.id} className="border-b border-slate-100">
                    <td className="py-2.5 font-medium text-ink">{c.razonSocial}</td>
                    <td className="py-2.5 text-slate-600">{c.nombreCorto || '—'}</td>
                    <td className="py-2.5">
                      {c.pendienteAsignacion ? (
                        <Badge color="amber">Pendiente de asignación</Badge>
                      ) : (
                        <Badge color="green">Con dosímetros</Badge>
                      )}
                    </td>
                  </tr>
                ))}
                {filtrados.length === 0 && (
                  <tr>
                    <td colSpan="3">
                      <EmptyState>No hay clientes con ese filtro.</EmptyState>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
            <Pagination page={page} totalPages={totalPages} onChange={setPage} />
          </div>
        )}
      </Card>
    </div>
  )
}
