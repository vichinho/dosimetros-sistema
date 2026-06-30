import { useEffect, useState } from 'react'
import {
  getTiposPorta,
  getTiposDosimetro,
  crearTipoPorta,
  actualizarTipoPorta,
  eliminarTipoPorta,
} from '../api/endpoints'
import { Card, Button, Input, Select, Badge, Loading, EmptyState } from '../components/ui'
import { useToast } from '../components/Toast'

const VACIO = { nombre: '', tipoDosimetroId: '' }

export default function TiposPorta() {
  const [portas, setPortas] = useState([])
  const [tipos, setTipos] = useState([])
  const [form, setForm] = useState(VACIO)
  const [editId, setEditId] = useState(null)
  const [loading, setLoading] = useState(true)
  const toast = useToast()

  const cargar = () =>
    getTiposPorta()
      .then(setPortas)
      .catch(() => toast.error('No se pudieron cargar los tipos de porta'))
      .finally(() => setLoading(false))

  useEffect(() => {
    cargar()
    getTiposDosimetro().then(setTipos).catch(() => {})
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const resetForm = () => {
    setForm(VACIO)
    setEditId(null)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const payload = { nombre: form.nombre.trim(), tipoDosimetroId: Number(form.tipoDosimetroId) }
    try {
      if (editId) {
        await actualizarTipoPorta(editId, payload)
        toast.success('Tipo de porta actualizado')
      } else {
        await crearTipoPorta(payload)
        toast.success('Tipo de porta creado')
      }
      resetForm()
      cargar()
    } catch (err) {
      toast.error(err.response?.data?.message || 'No se pudo guardar')
    }
  }

  const editar = (p) => {
    setEditId(p.id)
    setForm({ nombre: p.nombre, tipoDosimetroId: String(p.tipoDosimetroId) })
  }

  const eliminar = async (id) => {
    try {
      await eliminarTipoPorta(id)
      toast.success('Tipo de porta eliminado')
      if (editId === id) resetForm()
      cargar()
    } catch (err) {
      toast.error(err.response?.data?.message || 'No se pudo eliminar (puede estar en uso)')
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-ink">Tipos de porta</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Cada porta es compatible con un tipo de dosímetro (OSL, TLD o Cristal).
        </p>
      </div>

      <Card title={editId ? 'Editar tipo de porta' : 'Nuevo tipo de porta'}>
        <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
          <Input
            label="Nombre"
            value={form.nombre}
            onChange={(e) => setForm({ ...form, nombre: e.target.value })}
            required
          />
          <Select
            label="Compatible con (tipo de dosímetro)"
            value={form.tipoDosimetroId}
            onChange={(e) => setForm({ ...form, tipoDosimetroId: e.target.value })}
            required
          >
            <option value="">Selecciona…</option>
            {tipos.map((t) => (
              <option key={t.id} value={t.id}>{t.nombre}</option>
            ))}
          </Select>
          <div className="flex gap-2">
            <Button type="submit">{editId ? 'Guardar cambios' : 'Crear porta'}</Button>
            {editId && (
              <Button type="button" variant="secondary" onClick={resetForm}>
                Cancelar
              </Button>
            )}
          </div>
        </form>
      </Card>

      <Card title={`Tipos de porta (${portas.length})`}>
        {loading ? (
          <Loading />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-slate-500 border-b border-slate-200">
                  <th className="py-2 font-medium">Porta</th>
                  <th className="py-2 font-medium">Compatible con</th>
                  <th className="py-2"></th>
                </tr>
              </thead>
              <tbody>
                {portas.map((p) => (
                  <tr key={p.id} className="border-b border-slate-100">
                    <td className="py-2.5 font-medium text-ink">{p.nombre}</td>
                    <td className="py-2.5">
                      <Badge color="blue">{p.tipoDosimetroNombre}</Badge>
                    </td>
                    <td className="py-2.5 text-right space-x-3">
                      <button onClick={() => editar(p)} className="text-steel hover:underline text-sm">
                        Editar
                      </button>
                      <button onClick={() => eliminar(p.id)} className="text-red-600 hover:underline text-sm">
                        Eliminar
                      </button>
                    </td>
                  </tr>
                ))}
                {portas.length === 0 && (
                  <tr>
                    <td colSpan="3">
                      <EmptyState>No hay tipos de porta</EmptyState>
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
