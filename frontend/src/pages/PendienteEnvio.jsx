import { useEffect, useMemo, useState } from 'react'
import { useAuth } from '../auth/AuthContext'
import {
  getPendientesEnvio,
  getMisPendientesEnvio,
  marcarEnvioAsignaciones,
  getEjecutivos,
} from '../api/endpoints'
import { Card, Select, Input, Button, Badge, Loading, EmptyState, Pagination } from '../components/ui'
import { useToast } from '../components/Toast'

const POR_PAGINA = 10

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

  const [tab, setTab] = useState('pendientes') // 'pendientes' | 'enviados'
  const [asignaciones, setAsignaciones] = useState([])
  const [ejecutivos, setEjecutivos] = useState([])
  const [ejecutivoId, setEjecutivoId] = useState('')
  const [trimestresSel, setTrimestresSel] = useState(() => new Set()) // vacío = todos
  const [busqueda, setBusqueda] = useState('')
  const [orden, setOrden] = useState({ campo: 'trimestre', dir: 'desc' })
  const [seleccion, setSeleccion] = useState(() => new Set())
  const [page, setPage] = useState(1)
  const [loading, setLoading] = useState(true)
  const [marcando, setMarcando] = useState(false)

  const enviadosTab = tab === 'enviados'

  const cargar = () => {
    setLoading(true)
    setSeleccion(new Set())
    const fetcher = esEjecutivo ? getMisPendientesEnvio : getPendientesEnvio
    const params = { enviado: enviadosTab }
    if (!esEjecutivo && ejecutivoId) params.ejecutivoId = ejecutivoId
    fetcher(params)
      .then(setAsignaciones)
      .catch(() => toast.error('No se pudieron cargar las asignaciones'))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    if (!esEjecutivo) getEjecutivos().then(setEjecutivos).catch(() => {})
    cargar()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [ejecutivoId, tab])

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
    const q = busqueda.trim()
    if (q) lista = lista.filter((a) => String(a.numeroDosimetro).includes(q))
    const dir = orden.dir === 'asc' ? 1 : -1
    return [...lista].sort((x, y) => {
      const a = x[orden.campo], b = y[orden.campo]
      if (a == null) return 1
      if (b == null) return -1
      if (a < b) return -1 * dir
      if (a > b) return 1 * dir
      return 0
    })
  }, [asignaciones, trimestresSel, busqueda, orden])

  // Reinicia la página cuando cambian filtros, orden o pestaña.
  useEffect(() => { setPage(1) }, [trimestresSel, busqueda, orden, tab, ejecutivoId])

  const totalPages = Math.ceil(filtradas.length / POR_PAGINA)
  const visibles = filtradas.slice((page - 1) * POR_PAGINA, page * POR_PAGINA)

  const ordenarPor = (campo) =>
    setOrden((o) => (o.campo === campo ? { campo, dir: o.dir === 'asc' ? 'desc' : 'asc' } : { campo, dir: 'asc' }))

  const toggleSel = (id) => setSeleccion((prev) => {
    const next = new Set(prev)
    if (next.has(id)) next.delete(id); else next.add(id)
    return next
  })
  const toggleTodos = () =>
    setSeleccion((prev) => (prev.size === filtradas.length ? new Set() : new Set(filtradas.map((a) => a.id))))

  const cambiarEnvio = async () => {
    if (seleccion.size === 0) return
    setMarcando(true)
    const nuevoEnviado = !enviadosTab // en "pendientes" -> enviar; en "enviados" -> revertir
    try {
      const n = await marcarEnvioAsignaciones({ asignacionIds: [...seleccion], enviado: nuevoEnviado })
      toast.success(nuevoEnviado ? `${n} asignaciones marcadas como enviadas` : `${n} asignaciones devueltas a pendiente`)
      cargar()
    } catch (err) {
      toast.error(err.response?.data?.message || 'No se pudo actualizar el envío')
    } finally {
      setMarcando(false)
    }
  }

  const flechaOrden = (campo) => (orden.campo === campo ? (orden.dir === 'asc' ? '↑' : '↓') : '')

  const hayFiltros = busqueda !== '' || trimestresSel.size > 0 || (!esEjecutivo && ejecutivoId !== '')
  const limpiarFiltros = () => {
    setBusqueda('')
    setTrimestresSel(new Set())
    if (!esEjecutivo) setEjecutivoId('')
    setOrden({ campo: 'trimestre', dir: 'desc' })
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-ink">Pendiente de envío</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Dosímetros asignados que todavía no se han despachado físicamente.
        </p>
      </div>

      <div className="inline-flex rounded-lg border border-mist overflow-hidden">
        <button
          type="button"
          onClick={() => setTab('pendientes')}
          className={`px-4 py-1.5 text-sm ${!enviadosTab ? 'bg-steel text-white' : 'bg-white text-ink/70 hover:bg-mist/20'}`}
        >
          Pendientes
        </button>
        <button
          type="button"
          onClick={() => setTab('enviados')}
          className={`px-4 py-1.5 text-sm ${enviadosTab ? 'bg-steel text-white' : 'bg-white text-ink/70 hover:bg-mist/20'}`}
        >
          Enviados
        </button>
      </div>

      <Card
        title="Filtros"
        action={
          hayFiltros && (
            <button type="button" onClick={limpiarFiltros} className="text-sm text-steel hover:underline">
              Limpiar filtros
            </button>
          )
        }
      >
        <div className="divide-y divide-mist/50">
          {/* Búsqueda y ejecutivo */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 pb-5">
            <Input
              label="N° de dosímetro"
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              placeholder="Buscar… ej: 12345"
            />
            {!esEjecutivo && (
              <Select label="Ejecutivo" value={ejecutivoId} onChange={(e) => setEjecutivoId(e.target.value)}>
                <option value="">Todos los ejecutivos</option>
                {ejecutivos.map((ej) => (
                  <option key={ej.id} value={ej.id}>{ej.nombre}</option>
                ))}
              </Select>
            )}
          </div>

          {/* Trimestres */}
          <div className="py-5">
            <div className="flex items-center justify-between mb-2">
              <p className="text-xs font-semibold uppercase tracking-wide text-ink/50">Trimestre</p>
              <span className="text-xs text-ink/40">
                {trimestresSel.size === 0 ? 'Todos' : `${trimestresSel.size} seleccionados`}
              </span>
            </div>
            {trimestresDisponibles.length === 0 ? (
              <p className="text-sm text-slate-400">Sin trimestres</p>
            ) : (
              <div className="flex flex-wrap gap-2">
                {trimestresDisponibles.map((t) => (
                  <button
                    key={t}
                    type="button"
                    onClick={() => toggleTrimestre(t)}
                    className={`px-3 py-1 rounded-full border text-sm transition ${
                      trimestresSel.has(t)
                        ? 'bg-steel text-white border-steel'
                        : 'bg-white text-ink/70 border-mist hover:border-steel/50'
                    }`}
                  >
                    {t}
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Orden */}
          <div className="pt-5">
            <p className="text-xs font-semibold uppercase tracking-wide text-ink/50 mb-2">Ordenar por</p>
            <div className="flex flex-wrap gap-2">
              {CAMPOS_ORDEN.map((c) => {
                const activo = orden.campo === c.k
                return (
                  <button
                    key={c.k}
                    type="button"
                    onClick={() => ordenarPor(c.k)}
                    className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg border text-sm transition ${
                      activo
                        ? 'bg-steel/10 border-steel text-steel font-medium'
                        : 'bg-white text-ink/70 border-mist hover:border-steel/50'
                    }`}
                  >
                    {c.label}
                    <span className={activo ? 'opacity-100' : 'opacity-0'}>
                      {orden.dir === 'asc' ? '↑' : '↓'}
                    </span>
                  </button>
                )
              })}
            </div>
          </div>
        </div>
      </Card>

      <Card
        title={`${enviadosTab ? 'Enviados' : 'Pendientes'} (${filtradas.length})`}
        action={
          !esEjecutivo && (
            <Button onClick={cambiarEnvio} disabled={marcando || seleccion.size === 0}>
              {marcando
                ? 'Guardando…'
                : enviadosTab
                  ? `Revertir a pendiente (${seleccion.size})`
                  : `Marcar como enviado (${seleccion.size})`}
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
                  {enviadosTab && <th className="py-2 font-medium">Fecha envío</th>}
                  <th className="py-2 font-medium">Porta</th>
                  <th className="py-2 font-medium">Tarea</th>
                  <th className="py-2 font-medium">Trello</th>
                </tr>
              </thead>
              <tbody>
                {visibles.map((a) => (
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
                    {enviadosTab && <td className="py-2 text-slate-600">{a.fechaEnvio || '—'}</td>}
                    <td className="py-2 text-slate-600">{a.tipoPortaNombre}</td>
                    <td className="py-2 text-slate-600">{a.numeroTarea || '—'}</td>
                    <td className="py-2 text-slate-600">
                      {a.linkTrello ? <a href={a.linkTrello} target="_blank" rel="noreferrer" className="text-steel hover:underline">link ↗</a> : '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination page={page} totalPages={totalPages} onChange={setPage} />
          </div>
        )}
      </Card>
    </div>
  )
}
