import { useEffect, useState } from 'react'
import {
  asignarMasivo,
  getClientes,
  getEjecutivos,
  getEmpresas,
  getTiposPorta,
  getTareas,
} from '../api/endpoints'
import { Card, Button, Input, Select, Alert } from '../components/ui'
import { useToast } from '../components/Toast'

export default function Asignar() {
  const [clientes, setClientes] = useState([])
  const [ejecutivos, setEjecutivos] = useState([])
  const [empresas, setEmpresas] = useState([])
  const [portas, setPortas] = useState([])
  const [tareas, setTareas] = useState([])

  const [form, setForm] = useState({
    clienteId: '',
    ejecutivoId: '',
    empresaId: '',
    tipoPortaId: '',
    trimestre: '',
    cantidad: '',
    linkTrello: '',
  })
  const [tareasSeleccionadas, setTareasSeleccionadas] = useState([])
  const [resultado, setResultado] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const toast = useToast()

  useEffect(() => {
    getClientes().then(setClientes).catch(() => {})
    getEjecutivos().then(setEjecutivos).catch(() => {})
    getEmpresas().then(setEmpresas).catch(() => {})
    getTiposPorta().then(setPortas).catch(() => {})
    getTareas().then(setTareas).catch(() => {})
  }, [])

  const set = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

  const toggleTarea = (id) => {
    setTareasSeleccionadas((prev) =>
      prev.includes(id) ? prev.filter((t) => t !== id) : [...prev, id]
    )
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setResultado(null)
    if (tareasSeleccionadas.length === 0) {
      setError('Selecciona al menos una tarea')
      return
    }
    setLoading(true)
    try {
      const data = await asignarMasivo({
        clienteId: Number(form.clienteId),
        ejecutivoId: Number(form.ejecutivoId),
        empresaId: Number(form.empresaId),
        tipoPortaId: Number(form.tipoPortaId),
        trimestre: form.trimestre,
        cantidad: Number(form.cantidad),
        tareaIds: tareasSeleccionadas,
        linkTrello: form.linkTrello || null,
      })
      setResultado(data)
      toast.success(`${data.cantidadAsignada} dosímetros asignados`)
    } catch (err) {
      const msg = err.response?.data?.message || 'No se pudo realizar la asignación'
      setError(msg)
      toast.error(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-6 max-w-4xl">
      <h1 className="text-2xl font-bold text-ink">Asignación masiva</h1>

      <form onSubmit={handleSubmit} className="space-y-6">
        <Card title="Datos de la asignación">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Select label="Cliente" value={form.clienteId} onChange={set('clienteId')} required>
              <option value="">Selecciona…</option>
              {clientes.map((c) => (
                <option key={c.id} value={c.id}>{c.razonSocial}</option>
              ))}
            </Select>
            <Select label="Ejecutivo" value={form.ejecutivoId} onChange={set('ejecutivoId')} required>
              <option value="">Selecciona…</option>
              {ejecutivos.map((e) => (
                <option key={e.id} value={e.id}>{e.nombre}</option>
              ))}
            </Select>
            <Select label="Empresa" value={form.empresaId} onChange={set('empresaId')} required>
              <option value="">Selecciona…</option>
              {empresas.map((e) => (
                <option key={e.id} value={e.id}>{e.nombre}</option>
              ))}
            </Select>
            <Select label="Tipo de porta" value={form.tipoPortaId} onChange={set('tipoPortaId')} required>
              <option value="">Selecciona…</option>
              {portas.map((p) => (
                <option key={p.id} value={p.id}>{p.nombre}</option>
              ))}
            </Select>
            <Input label="Trimestre (ej. 2T2026)" value={form.trimestre} onChange={set('trimestre')} required />
            <Input
              label="Cantidad"
              type="number"
              min="1"
              value={form.cantidad}
              onChange={set('cantidad')}
              required
            />
            <Input
              label="Link de Trello (opcional)"
              value={form.linkTrello}
              onChange={set('linkTrello')}
              className="md:col-span-2"
            />
          </div>
        </Card>

        <Card title="Tareas disponibles">
          <p className="text-sm text-slate-500 mb-3">
            El sistema elige automáticamente los dosímetros compatibles dentro de las tareas marcadas.
          </p>
          <div className="grid grid-cols-2 md:grid-cols-3 gap-2">
            {tareas.map((t) => (
              <label
                key={t.id}
                className={`flex items-center gap-2 px-3 py-2 rounded-lg border cursor-pointer text-sm ${
                  tareasSeleccionadas.includes(t.id)
                    ? 'border-blue-500 bg-blue-50'
                    : 'border-slate-200'
                }`}
              >
                <input
                  type="checkbox"
                  checked={tareasSeleccionadas.includes(t.id)}
                  onChange={() => toggleTarea(t.id)}
                />
                {t.numeroTarea}
              </label>
            ))}
            {tareas.length === 0 && <p className="text-sm text-slate-400">No hay tareas</p>}
          </div>
        </Card>

        <Alert type="error">{error}</Alert>

        {resultado && (
          <Alert type="success">
            Asignación exitosa: {resultado.cantidadAsignada} de {resultado.cantidadSolicitada}{' '}
            dosímetros asignados.
          </Alert>
        )}

        <Button type="submit" disabled={loading}>
          {loading ? 'Asignando…' : 'Asignar dosímetros'}
        </Button>
      </form>
    </div>
  )
}
