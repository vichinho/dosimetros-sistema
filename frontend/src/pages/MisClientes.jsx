import { useEffect, useState } from 'react'
import { getMisClientes } from '../api/endpoints'
import { Card, Alert, Badge, Loading, EmptyState } from '../components/ui'

export default function MisClientes() {
  const [clientes, setClientes] = useState([])
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

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-ink">Mis clientes</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          {pendientes.length} pendiente(s) de asignación de {clientes.length} cliente(s)
        </p>
      </div>

      <Alert type="error">{error}</Alert>

      <Card title={`Clientes (${clientes.length})`}>
        {loading ? (
          <Loading />
        ) : (
          <table className="w-full text-sm">
            <thead>
              <tr className="text-left text-slate-500 border-b border-slate-200">
                <th className="py-2 font-medium">Razón social</th>
                <th className="py-2 font-medium">Nombre fantasía</th>
                <th className="py-2 font-medium">Estado</th>
              </tr>
            </thead>
            <tbody>
              {clientes.map((c) => (
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
              {clientes.length === 0 && (
                <tr>
                  <td colSpan="3">
                    <EmptyState>No tienes clientes asignados</EmptyState>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        )}
      </Card>
    </div>
  )
}
