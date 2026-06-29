import { useEffect, useState } from 'react'
import { getEjecutivos, crearEjecutivo, desactivarEjecutivo } from '../api/endpoints'
import { Card, Button, Input, Alert, Badge } from '../components/ui'

export default function Ejecutivos() {
  const [ejecutivos, setEjecutivos] = useState([])
  const [form, setForm] = useState({ nombre: '', email: '' })
  const [error, setError] = useState('')
  const [ok, setOk] = useState('')

  const cargar = () =>
    getEjecutivos().then(setEjecutivos).catch(() => setError('No se pudieron cargar los ejecutivos'))

  useEffect(() => {
    cargar()
  }, [])

  const handleCrear = async (e) => {
    e.preventDefault()
    setError('')
    setOk('')
    try {
      await crearEjecutivo({ nombre: form.nombre, email: form.email || null })
      setForm({ nombre: '', email: '' })
      setOk('Ejecutivo creado')
      cargar()
    } catch {
      setError('No se pudo crear el ejecutivo')
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-800">Ejecutivos</h1>

      <Card title="Nuevo ejecutivo">
        <form onSubmit={handleCrear} className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
          <Input
            label="Nombre"
            value={form.nombre}
            onChange={(e) => setForm({ ...form, nombre: e.target.value })}
            required
          />
          <Input
            label="Email"
            type="email"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
          />
          <Button type="submit">Crear</Button>
        </form>
        <div className="mt-3 space-y-2">
          <Alert type="error">{error}</Alert>
          <Alert type="success">{ok}</Alert>
        </div>
      </Card>

      <Card title={`Ejecutivos activos (${ejecutivos.length})`}>
        <table className="w-full text-sm">
          <thead>
            <tr className="text-left text-slate-500 border-b border-slate-200">
              <th className="py-2">Nombre</th>
              <th className="py-2">Email</th>
              <th className="py-2">Estado</th>
              <th className="py-2"></th>
            </tr>
          </thead>
          <tbody>
            {ejecutivos.map((e) => (
              <tr key={e.id} className="border-b border-slate-100">
                <td className="py-2 font-medium text-slate-800">{e.nombre}</td>
                <td className="py-2">{e.email || '—'}</td>
                <td className="py-2">
                  <Badge color={e.activo ? 'green' : 'red'}>{e.activo ? 'Activo' : 'Inactivo'}</Badge>
                </td>
                <td className="py-2 text-right">
                  {e.activo && (
                    <button
                      onClick={() => desactivarEjecutivo(e.id).then(cargar)}
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
