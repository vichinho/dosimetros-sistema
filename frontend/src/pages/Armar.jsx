import { useEffect, useState } from 'react'
import { getTareas, getTiposPorta, actualizarTipoPortaRango } from '../api/endpoints'
import { Card, Button, Input, Alert } from '../components/ui'
import Combobox from '../components/Combobox'
import { useToast } from '../components/Toast'

export default function Armar() {
  const [tareas, setTareas] = useState([])
  const [portas, setPortas] = useState([])
  const [form, setForm] = useState({
    tareaId: '',
    bandejaDesde: '',
    bandejaHasta: '',
    slotDesde: '',
    slotHasta: '',
    tipoPortaId: '',
  })
  const [resultado, setResultado] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const toast = useToast()

  useEffect(() => {
    getTareas().then(setTareas).catch(() => {})
    getTiposPorta().then(setPortas).catch(() => {})
  }, [])

  const set = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setResultado(null)
    if (!form.tareaId) return setError('Selecciona una tarea')
    if (!form.tipoPortaId) return setError('Selecciona el tipo de porta')

    setLoading(true)
    try {
      const data = await actualizarTipoPortaRango({
        tareaId: Number(form.tareaId),
        bandejaDesde: Number(form.bandejaDesde),
        bandejaHasta: Number(form.bandejaHasta),
        slotDesde: form.slotDesde ? Number(form.slotDesde) : null,
        slotHasta: form.slotHasta ? Number(form.slotHasta) : null,
        tipoPortaId: Number(form.tipoPortaId),
      })
      setResultado(data)
      toast.success(`${data.dosimetrosActualizados} dosímetros armados`)
    } catch (err) {
      const msg = err.response?.data?.message || 'No se pudo armar el rango'
      setError(msg)
      toast.error(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-6 max-w-3xl">
      <div>
        <h1 className="text-2xl font-bold text-ink">Armar dosímetros</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Asigna un tipo de porta a los dosímetros de una tarea dentro de un rango de bandeja/slot.
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        <Card title="Tarea y porta">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Combobox
              label="Tarea"
              options={tareas.map((t) => ({ value: t.id, label: t.numeroTarea }))}
              value={form.tareaId}
              onChange={(v) => setForm((f) => ({ ...f, tareaId: v }))}
              required
            />
            <Combobox
              label="Tipo de porta a asignar"
              options={portas.map((p) => ({ value: p.id, label: p.nombre }))}
              value={form.tipoPortaId}
              onChange={(v) => setForm((f) => ({ ...f, tipoPortaId: v }))}
              required
            />
          </div>
        </Card>

        <Card title="Rango de bandeja y slot">
          <p className="text-sm text-slate-500 mb-4">
            La bandeja es obligatoria. El slot es opcional: déjalo vacío para abarcar toda la bandeja.
          </p>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Input label="Bandeja desde" type="number" min="1" value={form.bandejaDesde} onChange={set('bandejaDesde')} required />
            <Input label="Bandeja hasta" type="number" min="1" value={form.bandejaHasta} onChange={set('bandejaHasta')} required />
            <Input label="Slot desde" type="number" min="1" value={form.slotDesde} onChange={set('slotDesde')} />
            <Input label="Slot hasta" type="number" min="1" value={form.slotHasta} onChange={set('slotHasta')} />
          </div>
        </Card>

        <Alert type="error">{error}</Alert>
        {resultado && (
          <Alert type="success">
            Se armaron {resultado.dosimetrosActualizados} dosímetros con el porta seleccionado.
          </Alert>
        )}

        <Button type="submit" disabled={loading}>
          {loading ? 'Armando…' : 'Armar dosímetros'}
        </Button>
      </form>
    </div>
  )
}
