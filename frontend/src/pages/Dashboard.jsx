import { useEffect, useState } from 'react'
import {
  ResponsiveContainer,
  LineChart,
  Line,
  BarChart,
  Bar,
  PieChart,
  Pie,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  Cell,
} from 'recharts'
import { getKpis, getStockHistorico } from '../api/endpoints'
import { Card, Loading, Alert, EmptyState, Select, Input, Modal, Badge, Spinner } from '../components/ui'
import { useToast } from '../components/Toast'

// Trimestre en formato 'QTYYYY' (ej. 2T2025) → rango de meses legible.
const MESES_TRIMESTRE = {
  1: 'ene–mar',
  2: 'abr–jun',
  3: 'jul–sep',
  4: 'oct–dic',
}
function mesesDeTrimestre(t) {
  if (!t || t.length < 6) return ''
  const q = Number(t[0])
  const anio = t.slice(2)
  const meses = MESES_TRIMESTRE[q]
  return meses ? `${meses} ${anio}` : ''
}

// Barras de una sola serie: un solo tono (magnitud). La dona (identidad) usa varios.
const BAR_FILL = '#5b7065'
const ESTADO_COLOR = {
  disponible: '#5b7065',
  asignado: '#04202c',
  dañado: '#304040',
  baja: '#c9d1c8',
}
const AXIS_TICK = { fontSize: 12, fill: '#304040' }
const GRID_STROKE = '#dfe4dd'
const tooltipStyle = {
  contentStyle: {
    borderRadius: 12,
    border: '1px solid #c9d1c8',
    fontSize: 13,
    boxShadow: '0 4px 12px rgba(0,0,0,0.06)',
  },
}

function pct(part, total) {
  if (!total) return '0%'
  return `${Math.round((part / total) * 100)}%`
}

function KpiTile({ label, value, sub, accent, onClick }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className="text-left bg-white rounded-2xl border border-mist/60 p-5 hover:border-steel hover:shadow-sm transition group"
    >
      <div className="flex items-center justify-between">
        <p className="text-sm text-ink/60">{label}</p>
        <span className="text-ink/30 group-hover:text-steel text-xs">ver ▸</span>
      </div>
      <p className="text-3xl font-bold mt-1 text-ink">{value}</p>
      {sub && <p className="text-xs text-ink/50 mt-0.5">{sub}</p>}
      <div className={`mt-3 h-1 w-10 rounded-full ${accent}`} />
    </button>
  )
}

function BarPanel({ title, data, dataKey = 'nombre', horizontal = false }) {
  if (!data || data.length === 0) {
    return (
      <Card title={title}>
        <EmptyState>Sin datos</EmptyState>
      </Card>
    )
  }
  return (
    <Card title={title}>
      <ResponsiveContainer width="100%" height={Math.max(240, horizontal ? data.length * 40 : 260)}>
        {horizontal ? (
          <BarChart data={data} layout="vertical" margin={{ top: 4, right: 16, left: 8, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke={GRID_STROKE} />
            <XAxis type="number" allowDecimals={false} tick={AXIS_TICK} tickLine={false} axisLine={false} />
            <YAxis type="category" dataKey={dataKey} width={150} tick={AXIS_TICK} tickLine={false} axisLine={false} />
            <Tooltip {...tooltipStyle} cursor={{ fill: '#e2e6e0' }} />
            <Bar dataKey="cantidad" fill={BAR_FILL} radius={[0, 6, 6, 0]} maxBarSize={26} />
          </BarChart>
        ) : (
          <BarChart data={data} margin={{ top: 8, right: 8, left: -16, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke={GRID_STROKE} />
            <XAxis dataKey={dataKey} tick={AXIS_TICK} tickLine={false} axisLine={false} />
            <YAxis allowDecimals={false} tick={AXIS_TICK} tickLine={false} axisLine={false} />
            <Tooltip {...tooltipStyle} cursor={{ fill: '#e2e6e0' }} />
            <Bar dataKey="cantidad" fill={BAR_FILL} radius={[6, 6, 0, 0]} maxBarSize={56} />
          </BarChart>
        )}
      </ResponsiveContainer>
    </Card>
  )
}

// Tabla simple para los drill-down
function ConteoTabla({ filas, columna = 'nombre', etiqueta = 'Detalle' }) {
  if (!filas || filas.length === 0) return <EmptyState>Sin datos</EmptyState>
  const total = filas.reduce((a, f) => a + (f.cantidad || 0), 0)
  return (
    <table className="w-full text-sm">
      <thead>
        <tr className="text-left text-slate-500 border-b border-slate-200">
          <th className="py-2 font-medium">{etiqueta}</th>
          <th className="py-2 font-medium text-right">Cantidad</th>
          <th className="py-2 font-medium text-right">%</th>
        </tr>
      </thead>
      <tbody>
        {filas.map((f, i) => (
          <tr key={i} className="border-b border-slate-100">
            <td className="py-2 text-ink">{f[columna]}</td>
            <td className="py-2 text-right font-semibold text-ink">{f.cantidad}</td>
            <td className="py-2 text-right text-ink/50">{pct(f.cantidad, total)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

export default function Dashboard() {
  const [kpis, setKpis] = useState(null)
  const [trimestre, setTrimestre] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [drill, setDrill] = useState(null)
  // #4 Stock histórico por fecha de creación
  const hoy = new Date().toISOString().slice(0, 10)
  const [histFecha, setHistFecha] = useState(hoy)
  const [hist, setHist] = useState(null)
  const [histLoading, setHistLoading] = useState(false)
  const toast = useToast()

  useEffect(() => {
    setLoading(true)
    getKpis(trimestre)
      .then(setKpis)
      .catch(() => {
        setError('No se pudieron cargar los KPIs')
        toast.error('No se pudieron cargar los KPIs')
      })
      .finally(() => setLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [trimestre])

  useEffect(() => {
    if (!histFecha) return
    setHistLoading(true)
    getStockHistorico(histFecha)
      .then(setHist)
      .catch(() => toast.error('No se pudo cargar el stock histórico'))
      .finally(() => setHistLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [histFecha])

  if (loading && !kpis) return <Loading label="Cargando dashboard…" />

  const sufijo = trimestre ? ` · ${trimestre}` : ''
  const meses = mesesDeTrimestre(trimestre)
  const k = kpis || {}
  const actualizando = loading && kpis // recarga tras cambiar el trimestre

  // Contenido de cada drill-down (reutiliza los datos ya cargados)
  const DRILL = {
    total: {
      title: 'Distribución de dosímetros',
      content: (
        <div className="space-y-6">
          <div>
            <h4 className="text-sm font-semibold text-ink mb-2">Por estado</h4>
            <ConteoTabla filas={k.dosimetrosPorEstado} columna="clave" etiqueta="Estado" />
          </div>
          <div>
            <h4 className="text-sm font-semibold text-ink mb-2">Por tipo</h4>
            <ConteoTabla filas={k.dosimetrosPorTipo} etiqueta="Tipo" />
          </div>
        </div>
      ),
    },
    disponibles: {
      title: 'Disponibles por porta (listos para asignar)',
      content: <ConteoTabla filas={k.disponiblesPorPorta} etiqueta="Porta" />,
    },
    asignados: {
      title: 'Dosímetros asignados',
      content: (
        <div className="space-y-6">
          <div>
            <h4 className="text-sm font-semibold text-ink mb-2">Asignaciones por empresa{sufijo}</h4>
            <ConteoTabla filas={k.asignacionesPorEmpresa} etiqueta="Empresa" />
          </div>
          <div>
            <h4 className="text-sm font-semibold text-ink mb-2">Asignaciones por ejecutivo{sufijo}</h4>
            <ConteoTabla filas={k.asignacionesPorEjecutivo} etiqueta="Ejecutivo" />
          </div>
        </div>
      ),
    },
    danados: {
      title: 'Dosímetros dañados',
      content: (
        <div className="space-y-3">
          <p className="text-sm text-ink/70">
            Hay <b>{k.danados}</b> dosímetros marcados como dañados (no se pueden asignar).
          </p>
          <p className="text-sm text-ink/60">
            Revísalos en la sección <b>Stock</b> filtrando por estado <Badge color="amber">dañado</Badge>,
            donde puedes marcarlos como buenos.
          </p>
          <ConteoTabla filas={k.dosimetrosPorEstado} columna="clave" etiqueta="Estado" />
        </div>
      ),
    },
    clientes: {
      title: `Clientes atendidos${sufijo}`,
      content: <ConteoTabla filas={k.topClientes} etiqueta="Cliente" />,
    },
    asignaciones: {
      title: `Asignaciones${sufijo}`,
      content: (
        <div className="space-y-6">
          <div>
            <h4 className="text-sm font-semibold text-ink mb-2">
              {trimestre ? 'Por mes' : 'Por trimestre'}
            </h4>
            <ConteoTabla
              filas={trimestre ? k.asignacionesPorMes : k.asignacionesPorTrimestre}
              columna="clave"
              etiqueta={trimestre ? 'Mes' : 'Trimestre'}
            />
          </div>
          <div>
            <h4 className="text-sm font-semibold text-ink mb-2">Por tipo de porta{sufijo}</h4>
            <ConteoTabla filas={k.asignacionesPorTipoPorta} etiqueta="Porta" />
          </div>
        </div>
      ),
    },
  }

  const top3 = (k.topClientes || []).slice(0, 3)
  // Conteos distintos del trimestre (las listas ya vienen filtradas del backend)
  const nClientes = (k.topClientes || []).length
  const nEmpresas = (k.asignacionesPorEmpresa || []).length
  const nPortas = (k.asignacionesPorTipoPorta || []).length
  const nEjecutivos = (k.asignacionesPorEjecutivo || []).length

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-ink">Dashboard</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            {trimestre
              ? `Asignaciones del trimestre ${trimestre}${meses ? ` (${meses})` : ''} · toca un indicador para ver el detalle`
              : 'Resumen de stock y asignaciones · toca un indicador para ver el detalle'}
          </p>
        </div>
        <div className="w-full sm:w-56">
          <Select
            label="Filtrar por trimestre"
            value={trimestre}
            onChange={(e) => setTrimestre(e.target.value)}
          >
            <option value="">Todos los trimestres</option>
            {k.trimestresDisponibles?.map((t) => (
              <option key={t} value={t}>
                {t}
                {mesesDeTrimestre(t) ? ` (${mesesDeTrimestre(t)})` : ''}
              </option>
            ))}
          </Select>
        </div>
      </div>

      {actualizando && (
        <div className="inline-flex items-center gap-1.5 text-xs text-ink/50">
          <Spinner className="w-3.5 h-3.5" /> actualizando datos del trimestre…
        </div>
      )}

      <Alert type="error">{error}</Alert>

      {kpis && (
        <>
          {/* KPIs clicables: cambian según haya o no un trimestre filtrado */}
          <div className="grid grid-cols-2 lg:grid-cols-5 gap-4">
            {trimestre ? (
              <>
                <KpiTile label={`Asignaciones · ${trimestre}`} value={k.totalAsignaciones} accent="bg-mist" onClick={() => setDrill('asignaciones')} />
                <KpiTile label="Clientes atendidos" value={nClientes} accent="bg-ink" onClick={() => setDrill('clientes')} />
                <KpiTile label="Empresas" value={nEmpresas} accent="bg-steel" onClick={() => setDrill('asignados')} />
                <KpiTile label="Portas usadas" value={nPortas} accent="bg-sun" onClick={() => setDrill('asignaciones')} />
                <KpiTile label="Ejecutivos" value={nEjecutivos} accent="bg-[#7d9387]" onClick={() => setDrill('asignados')} />
              </>
            ) : (
              <>
                <KpiTile label="Total dosímetros" value={k.totalDosimetros} accent="bg-ink" onClick={() => setDrill('total')} />
                <KpiTile
                  label="Disponibles"
                  value={k.disponibles}
                  sub={`${pct(k.disponibles, k.totalDosimetros)} del total`}
                  accent="bg-steel"
                  onClick={() => setDrill('disponibles')}
                />
                <KpiTile
                  label="Asignados"
                  value={k.asignados}
                  sub={`${pct(k.asignados, k.totalDosimetros)} del total`}
                  accent="bg-sun"
                  onClick={() => setDrill('asignados')}
                />
                <KpiTile label="Dañados" value={k.danados} accent="bg-[#7d9387]" onClick={() => setDrill('danados')} />
                <KpiTile label="Asignaciones" value={k.totalAsignaciones} accent="bg-mist" onClick={() => setDrill('asignaciones')} />
              </>
            )}
          </div>

          {/* Estado (dona, solo inventario) + evolución de asignaciones */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {!trimestre && (
            <Card title="Dosímetros por estado">
              {k.dosimetrosPorEstado?.length > 0 ? (
                <ResponsiveContainer width="100%" height={260}>
                  <PieChart>
                    <Pie
                      data={k.dosimetrosPorEstado}
                      dataKey="cantidad"
                      nameKey="clave"
                      innerRadius={55}
                      outerRadius={90}
                      paddingAngle={2}
                      label={(e) => e.clave}
                    >
                      {k.dosimetrosPorEstado.map((e, i) => (
                        <Cell
                          key={i}
                          fill={ESTADO_COLOR[e.clave] || BAR_FILL}
                          stroke="#ffffff"
                          strokeWidth={2}
                        />
                      ))}
                    </Pie>
                    <Tooltip {...tooltipStyle} />
                    <Legend iconType="circle" />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <EmptyState>Sin datos</EmptyState>
              )}
            </Card>
            )}

            {(() => {
              const evolucion = trimestre ? k.asignacionesPorMes : k.asignacionesPorTrimestre
              const tituloEvolucion = trimestre
                ? `Asignaciones por mes${meses ? ` · ${meses}` : ''}`
                : 'Asignaciones por trimestre'
              return (
                <Card title={tituloEvolucion} className={trimestre ? 'lg:col-span-3' : 'lg:col-span-2'}>
                  {evolucion?.length > 0 ? (
                    <ResponsiveContainer width="100%" height={260}>
                      <LineChart data={evolucion} margin={{ top: 8, right: 16, left: -16, bottom: 0 }}>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} stroke={GRID_STROKE} />
                        <XAxis dataKey="clave" tick={AXIS_TICK} tickLine={false} axisLine={false} />
                        <YAxis allowDecimals={false} tick={AXIS_TICK} tickLine={false} axisLine={false} />
                        <Tooltip {...tooltipStyle} />
                        <Line type="monotone" dataKey="cantidad" stroke="#5b7065" strokeWidth={2.5} dot={{ r: 4, fill: '#5b7065' }} activeDot={{ r: 6 }} />
                      </LineChart>
                    </ResponsiveContainer>
                  ) : (
                    <EmptyState>Aún no hay asignaciones</EmptyState>
                  )}
                </Card>
              )
            })()}
          </div>

          {/* Desgloses */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {!trimestre && (
              <>
                <BarPanel title="Disponibles por porta (estado de armado)" data={k.disponiblesPorPorta} />
                <BarPanel title="Dosímetros por tipo" data={k.dosimetrosPorTipo} />
              </>
            )}
            <BarPanel title={`Asignaciones por empresa${sufijo}`} data={k.asignacionesPorEmpresa} />
            <BarPanel title={`Asignaciones por tipo de porta${sufijo}`} data={k.asignacionesPorTipoPorta} />
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <BarPanel title={`Asignaciones por ejecutivo${sufijo}`} data={k.asignacionesPorEjecutivo} horizontal />
            <BarPanel title={`Top 3 clientes${sufijo}`} data={top3} horizontal />
          </div>

          {/* #4 Stock histórico por fecha de creación */}
          <Card
            title="Stock histórico por fecha"
            action={
              <div className="flex items-center gap-2">
                {histLoading && <Spinner className="w-4 h-4" />}
                <div className="w-44">
                  <Input
                    type="date"
                    max={hoy}
                    value={histFecha}
                    onChange={(e) => setHistFecha(e.target.value)}
                  />
                </div>
              </div>
            }
          >
            <p className="text-sm text-ink/60 mb-4">
              Dosímetros que ya existían en el inventario a la fecha elegida, según
              su fecha de creación (no por asignación).
            </p>
            {hist ? (
              <div className="space-y-6">
                <div className="flex items-baseline gap-2">
                  <span className="text-3xl font-bold text-ink">{hist.total}</span>
                  <span className="text-sm text-ink/60">
                    dosímetros existían al {hist.fecha}
                  </span>
                </div>
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                  <div>
                    <h4 className="text-sm font-semibold text-ink mb-2">Por tipo de porta</h4>
                    <ConteoTabla filas={hist.porTipoPorta} etiqueta="Porta" />
                  </div>
                  <div>
                    <h4 className="text-sm font-semibold text-ink mb-2">Por tipo de dosímetro</h4>
                    <ConteoTabla filas={hist.porTipoDosimetro} etiqueta="Tipo" />
                  </div>
                </div>
              </div>
            ) : (
              <EmptyState>Elige una fecha para ver el stock histórico</EmptyState>
            )}
          </Card>
        </>
      )}

      {drill && DRILL[drill] && (
        <Modal title={DRILL[drill].title} onClose={() => setDrill(null)}>
          {DRILL[drill].content}
        </Modal>
      )}
    </div>
  )
}
