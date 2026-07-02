import { useEffect, useState } from 'react'
import {
  asignarMasivo,
  getClientes,
  getEjecutivos,
  getEmpresas,
  getTiposPorta,
  getTareasDisponibles,
} from '../api/endpoints'
import { Card, Button, Input, Alert } from '../components/ui'
import Combobox from '../components/Combobox'
import { useToast } from '../components/Toast'

const TRIMESTRE_REGEX = /^[1-4]T\d{4}$/

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
  }, [])

  // Solo tareas con dosímetros disponibles, compatibles con el porta elegido.
  useEffect(() => {
    const porta = portas.find((p) => String(p.id) === String(form.tipoPortaId))
    getTareasDisponibles(porta ? porta.tipoDosimetroId : undefined)
      .then(setTareas)
      .catch(() => setTareas([]))
    setTareasSeleccionadas([])
  }, [form.tipoPortaId, portas])

  const set = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

  // Trimestre: normaliza a mayúsculas y autocompleta el año si solo escribe "3T".
  const normalizaTrimestre = (valor) => {
    let t = valor.toUpperCase().replace(/\s/g, '')
    if (/^[1-4]T$/.test(t)) t = t + new Date().getFullYear()
    return t
  }

  const toggleTarea = (id) => {
    setTareasSeleccionadas((prev) =>
      prev.includes(id) ? prev.filter((t) => t !== id) : [...prev, id]
    )
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setResultado(null)

    // Validar cliente
    if (!form.clienteId) {
      setError('Selecciona un cliente válido de la lista')
      return
    }

    // Validar formato de trimestre (ej. 3T2026)
    const trimestre = normalizaTrimestre(form.trimestre)
    if (!TRIMESTRE_REGEX.test(trimestre)) {
      setError('El trimestre debe tener el formato 1T2026, 2T2026, etc.')
      return
    }

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
        trimestre,
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
            {/* Cliente: combobox buscable estilizado */}
            <Combobox
              label="Cliente"
              options={clientes.map((c) => ({ value: c.id, label: c.razonSocial }))}
              value={form.clienteId}
              onChange={(v) => setForm((f) => ({ ...f, clienteId: v }))}
              required
            />

            <Combobox
              label="Ejecutivo"
              options={ejecutivos.map((e) => ({ value: e.id, label: e.nombre }))}
              value={form.ejecutivoId}
              onChange={(v) => setForm((f) => ({ ...f, ejecutivoId: v }))}
              required
            />
            <Combobox
              label="Empresa"
              options={empresas.map((e) => ({ value: e.id, label: e.nombre }))}
              value={form.empresaId}
              onChange={(v) => setForm((f) => ({ ...f, empresaId: v }))}
              required
            />
            <Combobox
              label="Tipo de porta"
              options={portas.map((p) => ({ value: p.id, label: p.nombre }))}
              value={form.tipoPortaId}
              onChange={(v) => setForm((f) => ({ ...f, tipoPortaId: v }))}
              required
            />

            {/* Trimestre: valida formato y autocompleta el año */}
            <label className="block">
              <span className="block text-sm font-medium text-ink/70 mb-1.5">Trimestre</span>
              <input
                value={form.trimestre}
                onChange={set('trimestre')}
                onBlur={() => setForm((f) => ({ ...f, trimestre: normalizaTrimestre(f.trimestre) }))}
                placeholder="Ej: 3T  →  3T2026"
                pattern="[1-4]T[0-9]{4}"
                title="Formato: 1T2026, 2T2026, etc."
                className="w-full px-3 py-2 border border-mist rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-steel/40 focus:border-steel transition"
                required
              />
            </label>

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
            Solo se muestran tareas con dosímetros disponibles
            {form.tipoPortaId ? ' compatibles con el porta elegido' : ''}. El sistema elige
            automáticamente los dosímetros dentro de las tareas marcadas.
          </p>
          <div className="grid grid-cols-2 md:grid-cols-3 gap-2">
            {tareas.map((t) => (
              <label
                key={t.id}
                className={`flex items-center justify-between gap-2 px-3 py-2 rounded-lg border cursor-pointer text-sm ${
                  tareasSeleccionadas.includes(t.id) ? 'border-steel bg-steel/10' : 'border-mist'
                }`}
              >
                <span className="flex items-center gap-2">
                  <input
                    type="checkbox"
                    checked={tareasSeleccionadas.includes(t.id)}
                    onChange={() => toggleTarea(t.id)}
                  />
                  {t.numeroTarea}
                </span>
                <span className="text-xs text-steel font-medium">{t.disponibles}</span>
              </label>
            ))}
            {tareas.length === 0 && (
              <p className="text-sm text-slate-400">No hay tareas con dosímetros disponibles</p>
            )}
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
