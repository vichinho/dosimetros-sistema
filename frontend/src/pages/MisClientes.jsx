import { useEffect, useState } from 'react'
import { getMisClientes } from '../api/endpoints'
import { Card, Alert } from '../components/ui'

export default function MisClientes() {
  const [clientes, setClientes] = useState([])
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
  }, [])

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-ink">Mis clientes</h1>
      <Alert type="error">{error}</Alert>

      <Card title={`Clientes asignados a mí (${clientes.length})`}>
        <table className="w-full text-sm">
          <thead>
            <tr className="text-left text-slate-500 border-b border-slate-200">
              <th className="py-2">Razón social</th>
              <th className="py-2">Nombre corto</th>
            </tr>
          </thead>
          <tbody>
            {clientes.map((c) => (
              <tr key={c.id} className="border-b border-slate-100">
                <td className="py-2 font-medium text-ink">{c.razonSocial}</td>
                <td className="py-2">{c.nombreCorto || '—'}</td>
              </tr>
            ))}
            {clientes.length === 0 && (
              <tr>
                <td colSpan="2" className="py-6 text-center text-slate-400">
                  No tienes clientes asignados
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </Card>
    </div>
  )
}
