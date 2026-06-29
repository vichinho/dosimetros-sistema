import { useEffect, useState } from 'react'
import { getClientes, crearCliente, desactivarCliente } from '../api/endpoints'
import { Card, Button, Input, Alert, Badge } from '../components/ui'

export default function Clientes() {
  const [clientes, setClientes] = useState([])
  const [form, setForm] = useState({ razonSocial: '', nombreCorto: '' })
  const [error, setError] = useState('')
  const [ok, setOk] = useState('')

  const cargar = () => getClientes().then(setClientes).catch(() => setError('No se pudieron cargar los clientes'))

  useEffect(() => {
    cargar()
  }, [])

  const handleCrear = async (e) => {
    e.preventDefault()
    setError('')
    setOk('')
    try {
      await crearCliente({ razonSocial: form.razonSocial, nombreCorto: form.nombreCorto || null })
      setForm({ razonSocial: '', nombreCorto: '' })
      setOk('Cliente creado')
      cargar()
    } catch {
      setError('No se pudo crear el cliente')
    }
  }

  const handleDesactivar = async (id) => {
    await desactivarCliente(id)
    cargar()
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-800">Clientes</h1>

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
          <Button type="submit">Crear</Button>
        </form>
        <div className="mt-3 space-y-2">
          <Alert type="error">{error}</Alert>
          <Alert type="success">{ok}</Alert>
        </div>
      </Card>

      <Card title={`Clientes activos (${clientes.length})`}>
        <table className="w-full text-sm">
          <thead>
            <tr className="text-left text-slate-500 border-b border-slate-200">
              <th className="py-2">Razón social</th>
              <th className="py-2">Nombre corto</th>
              <th className="py-2">Estado</th>
              <th className="py-2"></th>
            </tr>
          </thead>
          <tbody>
            {clientes.map((c) => (
              <tr key={c.id} className="border-b border-slate-100">
                <td className="py-2 font-medium text-slate-800">{c.razonSocial}</td>
                <td className="py-2">{c.nombreCorto || '—'}</td>
                <td className="py-2">
                  <Badge color={c.activo ? 'green' : 'red'}>{c.activo ? 'Activo' : 'Inactivo'}</Badge>
                </td>
                <td className="py-2 text-right">
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
          </tbody>
        </table>
      </Card>
    </div>
  )
}
