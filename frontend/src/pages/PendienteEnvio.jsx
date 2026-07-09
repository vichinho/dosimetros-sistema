import { useEffect, useMemo, useState } from 'react'
import { useAuth } from '../auth/AuthContext'
import {
  getPendientesEnvio,
  getMisPendientesEnvio,
  marcarEnvioAsignaciones,
  getEjecutivos,
} from '../api/endpoints'
import { Card, Select, Button, Badge, Loading, EmptyState } from '../components/ui'
import { useToast } from '../components/Toast'

// Orden fino en el cliente (botoneras).
const CAMPOS_ORDEN = [
  { k: 'trimestre', label: 'Trimestre' },
  { k: 'fechaAsignacion', label: 'Fecha' },
  { k: 'clienteNombre', label: 'Cliente' },
  { k: 'numeroDosimetro', label: 'N° dosímetro' },
]

export default function PendienteEnvio() {
  const { rol } = useAuth()
  const esEjecutivo = rol === 'EJECUTIVO'
  const toast = useToast()

  const [asignaciones, setAsignaciones] = useState([])
  const [ejecutivos, setEjecutivos] = useState([])
  const [ejecutivoId, setEjecutivoId] = useState('')
  const [trimestresSel, setTrimestresSel] = useState(() => new Set()) // vacío = todos
  const [orden, setOrden] = useState({ campo: 'trimestre', dir: 'desc' })
  const [seleccion, setSeleccion] = useState(() => new Set())
  const [loading, setLoading] = useState(true)
  const [marcando, setMarcando] = useState(false)

  const cargar = () => {
    setLoading(true)
    setSeleccion(new Set())
    const fetcher = esEjecutivo ? getMisPendientesEnvio : getPendientesEnvio
    const params = {}
    if (!esEjecutivo && ejecutivoId) params.ejecutivoId = ejecutivoId
    fetcher(params)
      .then(setAsignaciones)
      .catch(() => toast.error('No se pudieron cargar las asignaciones pendientes'))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    if (!esEjecutivo) getEjecutivos().then(setEjecutivos).catch(() => {})
    cargar()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [ejecutivoId])

  // Trimestres disponibles en los datos, para el filtro configurable.
  const trimestresDisponibles = useMemo(
    () => [...new Set(asignaciones.map((a) => a.trimestre))].sort().reverse(),
    [asignaciones]
  )

  const toggleTrimestre = (t) => setTrimestresSel((prev) => {
    const next = new Set(prev)
    if (next.has(t)) next.delete(t); else next.add(t)
    return next
  })

  const filtradas = useMemo(() => {
    let lista = asignaciones
    if (trimestresSel.size > 0) lista = lista.filter((a) => trimestresSel.has(a.trimestre))
    const dir = orden.dir === 'asc' ? 1 : -1
    return [...lista].sort((x, y) => {
      const a = x[orden.campo], b = y[orden.campo]
      if (a == null) return 1
      if (b == null) return -1
      if (a < b) return -1 * dir
      if (a > b) return 1 * dir
      return 0
    })
  }, [asignaciones, trimestresSel, orden])

  const ordenarPor = (campo) =>
    setOrden((o) => (o.campo === campo ? { campo, dir: o.dir === 'asc' ? 'desc' : 'asc' } : { campo, dir: 'asc' }))

  const toggleSel = (id) => setSeleccion((prev) => {
    const next = new Set(prev)
    if (next.has(id)) next.delete(id); else next.add(id)
    return next
  })
  const toggleTodos = () =>
    setSeleccion((prev) => (prev.size === filtradas.length ? new Set() : new Set(filtradas.map((a) => a.id))))

  const marcarEnviado = async () => {
    if (seleccion.size === 0) return
    setMarcando(true)
    try {
      const n = await marcarEnvioAsignaciones({ asignacionIds: [...seleccion], enviado: true })
      toast.success(`${n} asignaciones marcadas como enviadas`)
      cargar()
    } catch (err) {
      toast.error(err.response?.data?.message || 'No se pudo marcar el envío')
    } finally {
      setMarcando(false)
    }
  }

  const flechaOrden = (campo) => (orden.campo === campo ? (orden.dir === 'asc' ? '↑' : '↓') : '')

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-ink">Pendiente de envío</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Dosímetros asignados que todavía no se han despachado físicamente.
        </p>
      </div>

      <Card title="Filtros">
        <div className="space-y-4">
          {!esEjecutivo && (
            <div className="max-w-xs">
              <Select label="Ejecutivo" value={ejecutivoId} onChange={(e) => setEjecutivoId(e.target.value)}>
                <option value="">Todos</option>
                {ejecutivos.map((ej) => (
                  <option key={ej.id} value={ej.id}>{ej.nombre}</option>
                ))}
              </Select>
            </div>
          )}
          <div>
            <p className="text-sm font-medium text-ink/70 mb-1.5">Trimestres (vacío = todos)</p>
            {trimestresDisponibles.length === 0 ? (
              <p className="text-sm text-slate-400">Sin trimestres</p>
            ) : (
              <div className="flex flex-wrap gap-2">
                {trimestresDisponibles.map((t) => (
                  <button
                    key={t}
                    type="button"
                    onClick={() => toggleTrimestre(t)}
                    className={`px-3 py-1 rounded-full border text-sm ${
                      trimestresSel.has(t) ? 'bg-steel text-white border-steel' : 'bg-white text-ink/70 border-mist hover:bg-mist/20'
                    }`}
                  >
                    {t}
                  </button>
                ))}
              </div>
            )}
          </div>
          <div>
            <p className="text-sm font-medium text-ink/70 mb-1.5">Ordenar por</p>
            <div className="flex flex-wrap gap-2">
              {CAMPOS_ORDEN.map((c) => (
                <button
                  key={c.k}
                  type="button"
                  onClick={() => ordenarPor(c.k)}
                  className={`px-3 py-1.5 rounded-lg border text-sm ${
                    orden.campo === c.k ? 'bg-steel/10 border-steel text-steel' : 'bg-white text-ink/70 border-mist hover:bg-mist/20'
                  }`}
                >
                  {c.label} {flechaOrden(c.k)}
                </button>
              ))}
            </div>
          </div>
        </div>
      </Card>

      <Card
        title={`Pendientes (${filtradas.length})`}
        action={
          !esEjecutivo && (
            <Button onClick={marcarEnviado} disabled={marcando || seleccion.size === 0}>
              {marcando ? 'Marcando…' : `Marcar como enviado (${seleccion.size})`}
            </Button>
          )
        }
      >
        {loading ? (
          <Loading />
        ) : filtradas.length === 0 ? (
          <EmptyState>No hay asignaciones pendientes de envío.</EmptyState>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-slate-500 border-b border-slate-200">
                  {!esEjecutivo && (
                    <th className="py-2 w-8">
                      <input type="checkbox" checked={seleccion.size === filtradas.length && filtradas.length > 0} onChange={toggleTodos} />
                    </th>
                  )}
                  <th className="py-2 font-medium">N° dosímetro</th>
                  <th className="py-2 font-medium">Cliente</th>
                  {!esEjecutivo && <th className="py-2 font-medium">Ejecutivo</th>}
                  <th className="py-2 font-medium">Empresa</th>
                  <th className="py-2 font-medium">Trimestre</th>
                  <th className="py-2 font-medium">Fecha asig.</th>
                  <th className="py-2 font-medium">Porta</th>
                  <th className="py-2 font-medium">Tarea</th>
                  <th className="py-2 font-medium">Trello</th>
                </tr>
              </thead>
              <tbody>
                {filtradas.map((a) => (
                  <tr key={a.id} className={`border-b border-slate-100 ${seleccion.has(a.id) ? 'bg-steel/5' : ''}`}>
                    {!esEjecutivo && (
                      <td className="py-2"><input type="checkbox" checked={seleccion.has(a.id)} onChange={() => toggleSel(a.id)} /></td>
                    )}
                    <td className="py-2 font-medium text-ink">{a.numeroDosimetro}</td>
                    <td className="py-2 text-slate-600">{a.clienteNombre}</td>
                    {!esEjecutivo && <td className="py-2 text-slate-600">{a.ejecutivoNombre}</td>}
                    <td className="py-2 text-slate-600">{a.empresaNombre}</td>
                    <td className="py-2"><Badge color="blue">{a.trimestre}</Badge></td>
                    <td className="py-2 text-slate-600">{a.fechaAsignacion}</td>
                    <td className="py-2 text-slate-600">{a.tipoPortaNombre}</td>
                    <td className="py-2 text-slate-600">{a.numeroTarea || '—'}</td>
                    <td className="py-2 text-slate-600">
                      {a.linkTrello ? <a href={a.linkTrello} target="_blank" rel="noreferrer" className="text-steel hover:underline">link ↗</a> : '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  )
}
