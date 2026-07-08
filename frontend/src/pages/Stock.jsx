import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  getStock,
  getStockMatriz,
  getTiposDosimetro,
  getTiposPorta,
  getStockPortas,
  exportarStockExcel,
  actualizarStockExcel,
  marcarDanado,
  marcarBueno,
  liberarDosimetro,
} from '../api/endpoints'
import { Card, Select, Badge, Button, Loading, EmptyState, Pagination, Alert } from '../components/ui'
import { useToast } from '../components/Toast'

const estadoColor = { disponible: 'green', asignado: 'blue', baja: 'red', dañado: 'amber' }
const POR_PAGINA = 20

// Arma la matriz tarea × porta a partir de las celdas planas del backend.
function armarMatriz(celdas) {
  const columnas = []
  const colVistas = new Map()
  const filasMap = new Map()
  for (const c of celdas) {
    if (!colVistas.has(c.tipoPortaId)) {
      colVistas.set(c.tipoPortaId, true)
      columnas.push({ id: c.tipoPortaId, nombre: c.tipoPortaNombre })
    }
    if (!filasMap.has(c.tareaId)) {
      filasMap.set(c.tareaId, { tareaId: c.tareaId, numeroTarea: c.numeroTarea, celdas: {}, total: 0 })
    }
    const fila = filasMap.get(c.tareaId)
    fila.celdas[c.tipoPortaId] = c.cantidad
    fila.total += c.cantidad
  }
  columnas.sort((a, b) => a.nombre.localeCompare(b.nombre))
  const filas = [...filasMap.values()].sort((a, b) =>
    String(a.numeroTarea).localeCompare(String(b.numeroTarea), undefined, { numeric: true })
  )
  const totalesCol = {}
  let totalGeneral = 0
  for (const col of columnas) totalesCol[col.id] = 0
  for (const fila of filas) {
    for (const col of columnas) {
      totalesCol[col.id] += fila.celdas[col.id] || 0
    }
    totalGeneral += fila.total
  }
  return { columnas, filas, totalesCol, totalGeneral }
}

export default function Stock() {
  const [tipos, setTipos] = useState([])
  const [portas, setPortas] = useState([])
  const [filtros, setFiltros] = useState({ tipoDosimetroId: '', tipoPortaId: '', estado: 'disponible' })
  const [vista, setVista] = useState('dinamica') // 'dinamica' (tarea × porta) | 'lista'
  const [dosimetros, setDosimetros] = useState([])
  const [matriz, setMatriz] = useState([])
  const [detallePortas, setDetallePortas] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(1)
  // #8 Actualización de stock por archivo (upsert)
  const [archivo, setArchivo] = useState(null)
  const [subiendo, setSubiendo] = useState(false)
  const [resultado, setResultado] = useState(null)
  const [errorArchivo, setErrorArchivo] = useState('')
  const toast = useToast()

  useEffect(() => {
    getTiposDosimetro().then(setTipos).catch(() => {})
    getTiposPorta().then(setPortas).catch(() => {})
    getStockPortas().then(setDetallePortas).catch(() => {})
  }, [])

  const construirParams = useCallback(() => {
    const params = {}
    if (filtros.tipoDosimetroId) params.tipoDosimetroId = filtros.tipoDosimetroId
    if (filtros.tipoPortaId) params.tipoPortaId = filtros.tipoPortaId
    if (filtros.estado) params.estado = filtros.estado
    return params
  }, [filtros])

  const cargar = useCallback(() => {
    setLoading(true)
    if (vista === 'dinamica') {
      const params = {}
      if (filtros.tipoDosimetroId) params.tipoDosimetroId = filtros.tipoDosimetroId
      if (filtros.estado) params.estado = filtros.estado
      getStockMatriz(params)
        .then(setMatriz)
        .catch(() => toast.error('No se pudo cargar la vista dinámica'))
        .finally(() => setLoading(false))
    } else {
      getStock(construirParams())
        .then((data) => {
          setDosimetros(data)
          setPage(1)
        })
        .catch(() => toast.error('No se pudo cargar el stock'))
        .finally(() => setLoading(false))
    }
  }, [vista, filtros, construirParams, toast])

  useEffect(() => {
    cargar()
  }, [cargar])

  const onExportar = async () => {
    try {
      const blob = await exportarStockExcel(construirParams())
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'stock.xlsx'
      a.click()
      URL.revokeObjectURL(url)
    } catch {
      toast.error('No se pudo exportar el stock')
    }
  }

  const recargarDetalle = () => getStockPortas().then(setDetallePortas).catch(() => {})

  const onActualizarArchivo = async (e) => {
    e.preventDefault()
    setErrorArchivo('')
    setResultado(null)
    if (!archivo) {
      setErrorArchivo('Selecciona un archivo .xlsx')
      return
    }
    setSubiendo(true)
    try {
      const data = await actualizarStockExcel(archivo)
      setResultado(data)
      toast.success(
        `Actualización: ${data.creados} creados, ${data.actualizados} actualizados, ${data.sinCambios} sin cambios`
      )
      cargar()
      recargarDetalle()
    } catch (err) {
      const msg = err.response?.data?.message || 'No se pudo actualizar el stock'
      setErrorArchivo(msg)
      toast.error(msg)
    } finally {
      setSubiendo(false)
    }
  }

  const conAccion = (fn, ok) => async (id) => {
    try {
      await fn(id)
      toast.success(ok)
      cargar()
      recargarDetalle()
    } catch (err) {
      toast.error(err.response?.data?.message || 'No se pudo completar la acción')
    }
  }
  const onMarcarDanado = conAccion(marcarDanado, 'Dosímetro marcado como dañado')
  const onLiberar = conAccion(liberarDosimetro, 'Dosímetro liberado (vuelve a disponible)')
  const onMarcarBueno = conAccion(marcarBueno, 'Dosímetro marcado como bueno (disponible)')

  const portasFiltradas = filtros.tipoDosimetroId
    ? portas.filter((p) => String(p.tipoDosimetroId) === String(filtros.tipoDosimetroId))
    : portas

  const totalDisponibles = detallePortas.reduce((acc, p) => acc + (p.cantidad || 0), 0)
  const totalPages = Math.ceil(dosimetros.length / POR_PAGINA)
  const visibles = dosimetros.slice((page - 1) * POR_PAGINA, page * POR_PAGINA)
  const pivot = useMemo(() => armarMatriz(matriz), [matriz])

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-ink">Stock de dosímetros</h1>

      {/* #5 Portas disponibles: se muestran TODAS, incluidas las que están en 0 */}
      <Card title={`Stock por porta · ${totalDisponibles} disponibles en total`}>
        {detallePortas.length === 0 ? (
          <EmptyState>No hay tipos de porta registrados</EmptyState>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3">
            {detallePortas.map((p) => {
              const enCero = !p.cantidad
              return (
                <div
                  key={p.tipoPortaId}
                  className={`border rounded-xl p-3 ${enCero ? 'border-mist/40 bg-mist/10' : 'border-mist/60'}`}
                >
                  <p className={`text-2xl font-bold ${enCero ? 'text-ink/30' : 'text-ink'}`}>{p.cantidad}</p>
                  <p className="text-sm font-medium text-ink/80">{p.portaNombre}</p>
                  <p className="text-xs text-slate-500">{p.tipoDosimetroNombre}</p>
                </div>
              )
            })}
          </div>
        )}
      </Card>

      {/* #8 Actualización de stock por archivo (upsert por número) */}
      <Card title="Actualizar stock por archivo">
        <form onSubmit={onActualizarArchivo} className="space-y-3">
          <p className="text-sm text-slate-500">
            Sube un <b>.xlsx</b> (columnas: numero_dosimetro, tipo_dosimetro, tipo_porta,
            numero_tarea, numero_bandeja, slot_bandeja). Se busca por <b>número</b>: si no
            existe se crea, si cambió se actualiza y si es idéntico se deja igual. Los
            dosímetros asignados o dados de baja no se modifican.
          </p>
          <div className="flex flex-wrap items-center gap-3">
            <input
              type="file"
              accept=".xlsx"
              onChange={(e) => setArchivo(e.target.files[0])}
              className="block text-sm text-slate-600 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:bg-steel file:text-white hover:file:bg-steel/90"
            />
            <Button type="submit" disabled={subiendo}>
              {subiendo ? 'Actualizando…' : 'Actualizar stock'}
            </Button>
          </div>
        </form>
        {errorArchivo && <div className="mt-3"><Alert type="error">{errorArchivo}</Alert></div>}
        {resultado && (
          <div className="mt-4">
            <div className="flex flex-wrap gap-4 text-sm mb-3">
              <span>Total filas: <b>{resultado.totalFilas}</b></span>
              <span className="text-emerald-600">Creados: <b>{resultado.creados}</b></span>
              <span className="text-steel">Actualizados: <b>{resultado.actualizados}</b></span>
              <span className="text-ink/60">Sin cambios: <b>{resultado.sinCambios}</b></span>
              <span className="text-red-600">Con problemas: <b>{resultado.fallidas}</b></span>
            </div>
            {resultado.errores?.length > 0 && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-3 max-h-60 overflow-auto">
                <ul className="text-sm text-red-700 space-y-1">
                  {resultado.errores.map((msg, i) => (
                    <li key={i}>{msg}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}
      </Card>

      {/* Filtros + selector de vista */}
      <Card>
        <div className="flex items-center gap-2 mb-4">
          <span className="text-sm font-medium text-ink/70">Vista:</span>
          <div className="inline-flex rounded-lg border border-mist overflow-hidden">
            <button
              type="button"
              onClick={() => setVista('dinamica')}
              className={`px-3 py-1.5 text-sm ${vista === 'dinamica' ? 'bg-steel text-white' : 'bg-white text-ink/70 hover:bg-mist/20'}`}
            >
              Tabla dinámica (tarea × porta)
            </button>
            <button
              type="button"
              onClick={() => setVista('lista')}
              className={`px-3 py-1.5 text-sm ${vista === 'lista' ? 'bg-steel text-white' : 'bg-white text-ink/70 hover:bg-mist/20'}`}
            >
              Lista detallada
            </button>
          </div>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Select
            label="Tipo de dosímetro"
            value={filtros.tipoDosimetroId}
            onChange={(e) => setFiltros({ ...filtros, tipoDosimetroId: e.target.value, tipoPortaId: '' })}
          >
            <option value="">Todos</option>
            {tipos.map((t) => (
              <option key={t.id} value={t.id}>{t.nombre}</option>
            ))}
          </Select>

          <Select
            label="Estado de armado (porta)"
            value={filtros.tipoPortaId}
            onChange={(e) => setFiltros({ ...filtros, tipoPortaId: e.target.value })}
            disabled={vista === 'dinamica'}
          >
            <option value="">Todos</option>
            {portasFiltradas.map((p) => (
              <option key={p.id} value={p.id}>{p.nombre}</option>
            ))}
          </Select>

          <Select
            label="Estado"
            value={filtros.estado}
            onChange={(e) => setFiltros({ ...filtros, estado: e.target.value })}
          >
            <option value="disponible">Disponible</option>
            <option value="asignado">Asignado</option>
            <option value="dañado">Dañado</option>
            <option value="baja">Baja</option>
            <option value="">Todos</option>
          </Select>
        </div>
        {vista === 'dinamica' && (
          <p className="text-xs text-slate-500 mt-2">
            La vista dinámica muestra los dosímetros armados (con tarea y porta). El
            filtro de porta se ignora aquí porque la porta es una columna.
          </p>
        )}
      </Card>

      {/* #6 Vista dinámica: matriz tarea × porta */}
      {vista === 'dinamica' &&
        (loading ? (
          <Loading />
        ) : (
          <Card title={`Matriz tarea × porta · ${pivot.totalGeneral} dosímetros`}>
            {pivot.filas.length === 0 ? (
              <EmptyState>No hay dosímetros armados con esos filtros</EmptyState>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="text-left text-slate-500 border-b border-slate-200">
                      <th className="py-2 pr-4 font-medium sticky left-0 bg-white">Tarea</th>
                      {pivot.columnas.map((c) => (
                        <th key={c.id} className="py-2 px-3 font-medium text-right whitespace-nowrap">{c.nombre}</th>
                      ))}
                      <th className="py-2 pl-3 font-medium text-right">Total</th>
                    </tr>
                  </thead>
                  <tbody>
                    {pivot.filas.map((f) => (
                      <tr key={f.tareaId} className="border-b border-slate-100">
                        <td className="py-2 pr-4 font-medium text-ink sticky left-0 bg-white">{f.numeroTarea}</td>
                        {pivot.columnas.map((c) => (
                          <td key={c.id} className="py-2 px-3 text-right text-slate-600">
                            {f.celdas[c.id] ? f.celdas[c.id] : <span className="text-slate-300">·</span>}
                          </td>
                        ))}
                        <td className="py-2 pl-3 text-right font-semibold text-ink">{f.total}</td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot>
                    <tr className="border-t-2 border-slate-200 font-semibold text-ink">
                      <td className="py-2 pr-4 sticky left-0 bg-white">Total</td>
                      {pivot.columnas.map((c) => (
                        <td key={c.id} className="py-2 px-3 text-right">{pivot.totalesCol[c.id]}</td>
                      ))}
                      <td className="py-2 pl-3 text-right">{pivot.totalGeneral}</td>
                    </tr>
                  </tfoot>
                </table>
              </div>
            )}
          </Card>
        ))}

      {/* Lista detallada (con acciones por dosímetro) */}
      {vista === 'lista' && (
        <Card
          title={`Resultados (${dosimetros.length})`}
          action={
            <Button variant="secondary" onClick={onExportar} disabled={dosimetros.length === 0}>
              Exportar a Excel
            </Button>
          }
        >
          {loading ? (
            <Loading />
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-slate-500 border-b border-slate-200">
                    <th className="py-2 font-medium">Número</th>
                    <th className="py-2 font-medium">Tipo</th>
                    <th className="py-2 font-medium">Porta</th>
                    <th className="py-2 font-medium">Tarea</th>
                    <th className="py-2 font-medium">Bandeja/Slot</th>
                    <th className="py-2 font-medium">Estado</th>
                    <th className="py-2 font-medium text-right">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {visibles.map((d) => (
                    <tr key={d.id} className="border-b border-slate-100">
                      <td className="py-2.5 font-medium text-ink">{d.numero}</td>
                      <td className="py-2.5 text-slate-600">{d.tipoDosimetroNombre}</td>
                      <td className="py-2.5 text-slate-600">{d.tipoPortaNombre || <span className="text-slate-400">—</span>}</td>
                      <td className="py-2.5 text-slate-600">{d.numeroTarea || <span className="text-slate-400">—</span>}</td>
                      <td className="py-2.5 text-slate-600">
                        {d.numeroBandeja != null ? `${d.numeroBandeja} / ${d.slotBandeja}` : '—'}
                      </td>
                      <td className="py-2.5">
                        <Badge color={estadoColor[d.estado] || 'slate'}>{d.estado}</Badge>
                      </td>
                      <td className="py-2.5 text-right">
                        {d.estado === 'asignado' && (
                          <button onClick={() => onLiberar(d.id)} className="text-steel hover:underline text-sm">
                            Liberar
                          </button>
                        )}
                        {d.estado === 'disponible' && (
                          <button onClick={() => onMarcarDanado(d.id)} className="text-amber-600 hover:underline text-sm">
                            Marcar dañado
                          </button>
                        )}
                        {d.estado === 'dañado' && (
                          <button onClick={() => onMarcarBueno(d.id)} className="text-steel hover:underline text-sm">
                            Marcar bueno
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {dosimetros.length === 0 && <EmptyState>No hay dosímetros con esos filtros</EmptyState>}
              <Pagination page={page} totalPages={totalPages} onChange={setPage} />
            </div>
          )}
        </Card>
      )}
    </div>
  )
}
