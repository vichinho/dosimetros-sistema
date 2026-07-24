import { useEffect, useMemo, useState } from 'react'
import { useAuth } from '../auth/AuthContext'
import {
  getClientes,
  getMisClientes,
  getEjecutivos,
  getResumenClienteTrimestre,
  getMisResumenClienteTrimestre,
} from '../api/endpoints'
import { Card, Select, Input, Badge, Loading, EmptyState, Pagination } from '../components/ui'
import { useToast } from '../components/Toast'

const POR_PAGINA = 10

export default function PendienteAsignacion() {
  const { rol } = useAuth()
  const esEjecutivo = rol === 'EJECUTIVO'
  const toast = useToast()

  const [clientes, setClientes] = useState([])
  const [conteos, setConteos] = useState([])
  const [ejecutivos, setEjecutivos] = useState([])
  const [ejecutivoId, setEjecutivoId] = useState('')
  const [trimestresSel, setTrimestresSel] = useState(() => new Set()) // vacío = todos (los disponibles)
  const [busqueda, setBusqueda] = useState('')
  const [soloPendientes, setSoloPendientes] = useState(false)
  const [page, setPage] = useState(1)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!esEjecutivo) getEjecutivos().then(setEjecutivos).catch(() => {})
  }, [esEjecutivo])

  const cargar = () => {
    setLoading(true)
    const pClientes = esEjecutivo
      ? getMisClientes()
      : getClientes(ejecutivoId ? { ejecutivoId } : undefined)
    const pConteos = esEjecutivo
      ? getMisResumenClienteTrimestre()
      : getResumenClienteTrimestre(ejecutivoId ? { ejecutivoId } : undefined)
    Promise.all([pClientes, pConteos])
      .then(([cl, co]) => {
        setClientes(cl)
        setConteos(co)
      })
      .catch(() => toast.error('No se pudo cargar la información'))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    cargar()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [ejecutivoId])

  // Lookup clienteId -> { trimestre -> cantidad }
  const porCliente = useMemo(() => {
    const map = new Map()
    for (const c of conteos) {
      if (!map.has(c.clienteId)) map.set(c.clienteId, {})
      map.get(c.clienteId)[c.trimestre] = c.cantidad
    }
    return map
  }, [conteos])

  // Trimestres presentes en los datos (orden descendente por año y trimestre).
  const trimestresDisponibles = useMemo(() => {
    const set = new Set(conteos.map((c) => c.trimestre))
    return [...set].sort((a, b) => {
      const ay = a.slice(2), by = b.slice(2)
      if (ay !== by) return by.localeCompare(ay)
      return b.slice(0, 1).localeCompare(a.slice(0, 1))
    })
  }, [conteos])

  // Columnas a comparar: las seleccionadas, o todas si no hay selección.
  const columnas = useMemo(() => {
    const base = trimestresSel.size > 0
      ? trimestresDisponibles.filter((t) => trimestresSel.has(t))
      : trimestresDisponibles
    return base
  }, [trimestresDisponibles, trimestresSel])

  const toggleTrimestre = (t) => setTrimestresSel((prev) => {
    const next = new Set(prev)
    if (next.has(t)) next.delete(t); else next.add(t)
    return next
  })

  // Filas: clientes filtrados por búsqueda y (opcional) solo pendientes en la 1ª columna.
  const filas = useMemo(() => {
    const q = busqueda.trim().toLowerCase()
    let lista = clientes
    if (q) lista = lista.filter((c) => c.razonSocial.toLowerCase().includes(q))
    if (soloPendientes && columnas.length > 0) {
      const col = columnas[0]
      lista = lista.filter((c) => !(porCliente.get(c.id)?.[col] > 0))
    }
    return lista
  }, [clientes, busqueda, soloPendientes, columnas, porCliente])

  useEffect(() => { setPage(1) }, [busqueda, soloPendientes, trimestresSel, ejecutivoId])

  const totalPages = Math.ceil(filas.length / POR_PAGINA)
  const visibles = filas.slice((page - 1) * POR_PAGINA, page * POR_PAGINA)

  const colLabel = columnas.length ? columnas[0] : null

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-ink">Pendiente de asignación</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Comparación por trimestre: cuántos dosímetros tiene cada cliente en cada período.
          Donde no hay asignación, el cliente está <b>pendiente</b>.
        </p>
      </div>

      <Card title="Filtros">
        <div className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            <Input label="Buscar cliente" value={busqueda} onChange={(e) => setBusqueda(e.target.value)} placeholder="Razón social…" />
            {!esEjecutivo && (
              <Select label="Ejecutivo" value={ejecutivoId} onChange={(e) => setEjecutivoId(e.target.value)}>
                <option value="">Todos los ejecutivos</option>
                {ejecutivos.map((ej) => (
                  <option key={ej.id} value={ej.id}>{ej.nombre}</option>
                ))}
              </Select>
            )}
          </div>
          <div>
            <div className="flex items-center justify-between mb-2">
              <p className="text-xs font-semibold uppercase tracking-wide text-ink/50">Trimestres a comparar</p>
              <span className="text-xs text-ink/40">{trimestresSel.size === 0 ? 'Todos' : `${trimestresSel.size} seleccionados`}</span>
            </div>
            {trimestresDisponibles.length === 0 ? (
              <p className="text-sm text-slate-400">Sin trimestres con asignaciones</p>
            ) : (
              <div className="flex flex-wrap gap-2">
                {trimestresDisponibles.map((t) => (
                  <button key={t} type="button" onClick={() => toggleTrimestre(t)}
                    className={`px-3 py-1 rounded-full border text-sm transition ${
                      trimestresSel.has(t) ? 'bg-steel text-white border-steel' : 'bg-white text-ink/70 border-mist hover:border-steel/50'
                    }`}>
                    {t}
                  </button>
                ))}
              </div>
            )}
          </div>
          {colLabel && (
            <label className="flex items-center gap-2 text-sm text-ink/70">
              <input type="checkbox" checked={soloPendientes} onChange={(e) => setSoloPendientes(e.target.checked)} />
              Mostrar solo clientes pendientes en <b>{colLabel}</b>
            </label>
          )}
        </div>
      </Card>

      <Card title={`Clientes (${filas.length})`}>
        {loading ? (
          <Loading />
        ) : columnas.length === 0 ? (
          <EmptyState>No hay trimestres con asignaciones para comparar.</EmptyState>
        ) : filas.length === 0 ? (
          <EmptyState>No hay clientes con ese filtro.</EmptyState>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-slate-500 border-b border-slate-200">
                  <th className="py-2 font-medium sticky left-0 bg-white">Cliente</th>
                  {columnas.map((t) => (
                    <th key={t} className="py-2 px-3 font-medium text-center whitespace-nowrap">{t}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {visibles.map((c) => {
                  const conteo = porCliente.get(c.id) || {}
                  return (
                    <tr key={c.id} className="border-b border-slate-100">
                      <td className="py-2 pr-4 font-medium text-ink sticky left-0 bg-white">{c.razonSocial}</td>
                      {columnas.map((t) => {
                        const n = conteo[t] || 0
                        return (
                          <td key={t} className="py-2 px-3 text-center">
                            {n > 0 ? (
                              <span className="font-semibold text-ink">{n}</span>
                            ) : (
                              <Badge color="amber">Pendiente</Badge>
                            )}
                          </td>
                        )
                      })}
                    </tr>
                  )
                })}
              </tbody>
            </table>
            <Pagination page={page} totalPages={totalPages} onChange={setPage} />
          </div>
        )}
      </Card>
    </div>
  )
}
