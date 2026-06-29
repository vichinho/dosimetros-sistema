import { useEffect, useState } from 'react'
import { getClientes, crearCliente, desactivarCliente } from '../api/endpoints'
import { Card, Button, Input, Badge, Loading, EmptyState } from '../components/ui'
import { useToast } from '../components/Toast'

export default function Clientes() {
  const [clientes, setClientes] = useState([])
  const [form, setForm] = useState({ razonSocial: '', nombreCorto: '' })
  const [loading, setLoading] = useState(true)
  const toast = useToast()

  const cargar = () =>
    getClientes()
      .then(setClientes)
      .catch(() => toast.error('No se pudieron cargar los clientes'))
      .finally(() => setLoading(false))

  useEffect(() => {
    cargar()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const handleCrear = async (e) => {
    e.preventDefault()
    try {
      await crearCliente({ razonSocial: form.razonSocial, nombreCorto: form.nombreCorto || null })
      setForm({ razonSocial: '', nombreCorto: '' })
      toast.success('Cliente creado correctamente')
      cargar()
    } catch {
      toast.error('No se pudo crear el cliente')
    }
  }

  const handleDesactivar = async (id) => {
    try {
      await desactivarCliente(id)
      toast.success('Cliente desactivado')
      cargar()
    } catch {
      toast.error('No se pudo desactivar')
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-ink">Clientes</h1>

      <Card title="Nuevo cliente">
        <form onSubmit={handleCrear} className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
          <Input
            label="Razón social"
            value={form.razonSocial}
            onChange={(e) => setForm({ ...form, razonSocial: e.target.value })}
            required
          />
          <Input
            label="Nombre corto"
            value={form.nombreCorto}
            onChange={(e) => setForm({ ...form, nombreCorto: e.target.value })}
          />
          <Button type="submit">Crear cliente</Button>
        </form>
      </Card>

      <Card title={`Clientes activos (${clientes.length})`}>
        {loading ? (
          <Loading />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-slate-500 border-b border-slate-200">
                  <th className="py-2 font-medium">Razón social</th>
                  <th className="py-2 font-medium">Nombre corto</th>
                  <th className="py-2 font-medium">Estado</th>
                  <th className="py-2"></th>
                </tr>
              </thead>
              <tbody>
                {clientes.map((c) => (
                  <tr key={c.id} className="border-b border-slate-100">
                    <td className="py-2.5 font-medium text-ink">{c.razonSocial}</td>
                    <td className="py-2.5 text-slate-600">{c.nombreCorto || '—'}</td>
                    <td className="py-2.5">
                      <Badge color={c.activo ? 'green' : 'red'}>{c.activo ? 'Activo' : 'Inactivo'}</Badge>
                    </td>
                    <td className="py-2.5 text-right">
                      {c.activo && (
                        <button
                          onClick={() => handleDesactivar(c.id)}
                          className="text-red-600 hover:underline text-sm"
                        >
                          Desactivar
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
                {clientes.length === 0 && (
                  <tr>
                    <td colSpan="4">
                      <EmptyState>No hay clientes registrados</EmptyState>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  )
}
