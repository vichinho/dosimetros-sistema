import { useEffect, useState } from 'react'
import { getEjecutivos, crearEjecutivo, desactivarEjecutivo } from '../api/endpoints'
import { Card, Button, Input, Badge, Loading, EmptyState } from '../components/ui'
import { useToast } from '../components/Toast'

function iniciales(nombre) {
  return nombre
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((p) => p[0])
    .join('')
    .toUpperCase()
}

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
      <h1 className="text-2xl font-bold text-ink">Ejecutivos</h1>

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

      <div>
        <h2 className="text-base font-semibold text-ink mb-3">
          Ejecutivos activos ({ejecutivos.length})
        </h2>
        {loading ? (
          <Loading />
        ) : ejecutivos.length === 0 ? (
          <Card>
            <EmptyState>No hay ejecutivos registrados</EmptyState>
          </Card>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {ejecutivos.map((e) => (
              <div
                key={e.id}
                className="bg-white rounded-2xl border border-mist/60 p-5 flex flex-col gap-4"
              >
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 rounded-full bg-steel/15 text-steel flex items-center justify-center font-bold">
                    {iniciales(e.nombre)}
                  </div>
                  <div className="min-w-0 flex-1">
                    <p className="font-semibold text-ink truncate">{e.nombre}</p>
                    <p className="text-xs text-ink/50 truncate">{e.email || 'Sin email'}</p>
                  </div>
                  <Badge color={e.activo ? 'green' : 'red'}>{e.activo ? 'Activo' : 'Inactivo'}</Badge>
                </div>
                {e.activo && (
                  <div className="flex justify-end">
                    <button
                      onClick={() => handleDesactivar(e.id)}
                      className="text-sm text-red-600 hover:underline"
                    >
                      Desactivar
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
