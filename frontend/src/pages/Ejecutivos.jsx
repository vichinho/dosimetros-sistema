import { useEffect, useState } from 'react'
import { getEjecutivos, crearEjecutivo, desactivarEjecutivo } from '../api/endpoints'
import { Card, Button, Input, Badge, Loading, EmptyState } from '../components/ui'
import { useToast } from '../components/Toast'

export default function Ejecutivos() {
  const [ejecutivos, setEjecutivos] = useState([])
  const [form, setForm] = useState({ nombre: '', email: '' })
  const [loading, setLoading] = useState(true)
  const toast = useToast()

  const cargar = () =>
    getEjecutivos()
      .then(setEjecutivos)
      .catch(() => toast.error('No se pudieron cargar los ejecutivos'))
      .finally(() => setLoading(false))

  useEffect(() => {
    cargar()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const handleCrear = async (e) => {
    e.preventDefault()
    try {
      await crearEjecutivo({ nombre: form.nombre, email: form.email || null })
      setForm({ nombre: '', email: '' })
      toast.success('Ejecutivo creado correctamente')
      cargar()
    } catch {
      toast.error('No se pudo crear el ejecutivo')
    }
  }

  const handleDesactivar = async (id) => {
    try {
      await desactivarEjecutivo(id)
      toast.success('Ejecutivo desactivado')
      cargar()
    } catch {
      toast.error('No se pudo desactivar')
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
          <Button type="submit">Crear ejecutivo</Button>
        </form>
      </Card>

      <Card title={`Ejecutivos activos (${ejecutivos.length})`}>
        {loading ? (
          <Loading />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-slate-500 border-b border-slate-200">
                  <th className="py-2 font-medium">Nombre</th>
                  <th className="py-2 font-medium">Email</th>
                  <th className="py-2 font-medium">Estado</th>
                  <th className="py-2"></th>
                </tr>
              </thead>
              <tbody>
                {ejecutivos.map((e) => (
                  <tr key={e.id} className="border-b border-slate-100">
                    <td className="py-2.5 font-medium text-slate-800">{e.nombre}</td>
                    <td className="py-2.5 text-slate-600">{e.email || '—'}</td>
                    <td className="py-2.5">
                      <Badge color={e.activo ? 'green' : 'red'}>{e.activo ? 'Activo' : 'Inactivo'}</Badge>
                    </td>
                    <td className="py-2.5 text-right">
                      {e.activo && (
                        <button
                          onClick={() => handleDesactivar(e.id)}
                          className="text-red-600 hover:underline text-sm"
                        >
                          Desactivar
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
                {ejecutivos.length === 0 && (
                  <tr>
                    <td colSpan="4">
                      <EmptyState>No hay ejecutivos registrados</EmptyState>
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
