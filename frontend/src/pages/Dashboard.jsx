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
import { Card, Loading, Alert, EmptyState, Select, Input, Modal, Badge } from '../components/ui'
import { useToast } from '../components/Toast'

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
  // Stock histórico (#4): stock existente a una fecha dada.
  const hoy = new Date().toISOString().slice(0, 10)
  const [fechaHist, setFechaHist] = useState(hoy)
  const [stockHist, setStockHist] = useState(null)
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

  // Stock histórico: se recarga al cambiar la fecha (con un pequeño debounce).
  useEffect(() => {
    if (!fechaHist) return
    setHistLoading(true)
    const t = setTimeout(() => {
      getStockHistorico(fechaHist)
        .then(setStockHist)
        .catch(() => toast.error('No se pudo cargar el stock histórico'))
        .finally(() => setHistLoading(false))
    }, 250)
    return () => clearTimeout(t)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [fechaHist])

  if (loading && !kpis) return <Loading label="Cargando dashboard…" />

  const sufijo = trimestre ? ` · ${trimestre}` : ''
  const k = kpis || {}

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
    asignaciones: {
      title: `Asignaciones${sufijo}`,
      content: (
        <div className="space-y-6">
          <div>
            <h4 className="text-sm font-semibold text-ink mb-2">Por trimestre</h4>
            <ConteoTabla filas={k.asignacionesPorTrimestre} columna="clave" etiqueta="Trimestre" />
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

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-ink flex items-center gap-2">
            Dashboard
            {trimestre ? (
              <Badge color="green">{trimestre}</Badge>
            ) : (
              <Badge color="slate">Todos los trimestres</Badge>
            )}
            {loading && kpis && (
              <span className="text-xs font-normal text-steel animate-pulse">actualizando…</span>
            )}
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Las asignaciones se actualizan al trimestre seleccionado · toca un indicador para ver el detalle
          </p>
        </div>
        <div className="w-full sm:w-56">
          <Select
            label="Filtrar asignaciones por trimestre"
            value={trimestre}
            onChange={(e) => setTrimestre(e.target.value)}
          >
            <option value="">Todos los trimestres</option>
            {k.trimestresDisponibles?.map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </Select>
        </div>
      </div>

      <Alert type="error">{error}</Alert>

      {kpis && (
        <>
          {/* KPIs clicables */}
          <div className="grid grid-cols-2 lg:grid-cols-5 gap-4">
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
            <KpiTile label={`Asignaciones${sufijo}`} value={k.totalAsignaciones} accent="bg-mist" onClick={() => setDrill('asignaciones')} />
          </div>

          {/* Estado (dona) + evolución por trimestre */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
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

            {(() => {
              // Con un trimestre seleccionado se muestra su desglose por mes;
              // sin filtro, la evolución por trimestre (#2/#3).
              const porMes = trimestre && k.asignacionesPorMes?.length > 0
              const serie = porMes ? k.asignacionesPorMes : k.asignacionesPorTrimestre
              const titulo = porMes
                ? `Asignaciones por mes · ${trimestre}`
                : 'Asignaciones por trimestre'
              return (
                <Card title={titulo} className="lg:col-span-2">
                  {serie?.length > 0 ? (
                    <ResponsiveContainer width="100%" height={260}>
                      <LineChart data={serie} margin={{ top: 8, right: 16, left: -16, bottom: 0 }}>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} stroke={GRID_STROKE} />
                        <XAxis dataKey="clave" tick={AXIS_TICK} tickLine={false} axisLine={false} />
                        <YAxis allowDecimals={false} tick={AXIS_TICK} tickLine={false} axisLine={false} />
                        <Tooltip {...tooltipStyle} />
                        <Line type="monotone" dataKey="cantidad" stroke="#5b7065" strokeWidth={2.5} dot={{ r: 4, fill: '#5b7065' }} activeDot={{ r: 6 }} />
                      </LineChart>
                    </ResponsiveContainer>
                  ) : (
                    <EmptyState>{porMes ? 'Sin asignaciones en este trimestre' : 'Aún no hay asignaciones'}</EmptyState>
                  )}
                </Card>
              )
            })()}
          </div>

          {/* Desgloses */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <BarPanel title="Disponibles por porta (estado de armado)" data={k.disponiblesPorPorta} />
            <BarPanel title="Dosímetros por tipo" data={k.dosimetrosPorTipo} />
            <BarPanel title={`Asignaciones por empresa${sufijo}`} data={k.asignacionesPorEmpresa} />
            <BarPanel title={`Asignaciones por tipo de porta${sufijo}`} data={k.asignacionesPorTipoPorta} />
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <BarPanel title={`Asignaciones por ejecutivo${sufijo}`} data={k.asignacionesPorEjecutivo} horizontal />
            <BarPanel title={`Top 3 clientes${sufijo}`} data={top3} horizontal />
          </div>

          {/* Stock histórico por fecha (#4) */}
          <Card title="Stock histórico por fecha">
            <div className="flex flex-col sm:flex-row sm:items-end gap-3 mb-4">
              <div className="w-full sm:w-56">
                <Input
                  label="Stock existente al"
                  type="date"
                  max={hoy}
                  value={fechaHist}
                  onChange={(e) => setFechaHist(e.target.value)}
                />
              </div>
              <p className="text-sm text-ink/60 pb-2">
                Dosímetros que ya existían en el inventario a esa fecha, según su
                fecha de ingreso.
                {stockHist && (
                  <>
                    {' '}Total: <b className="text-ink">{stockHist.total}</b>
                    {histLoading && <span className="text-steel animate-pulse"> · actualizando…</span>}
                  </>
                )}
              </p>
            </div>
            {stockHist?.porPorta?.length > 0 ? (
              <ResponsiveContainer width="100%" height={Math.max(220, stockHist.porPorta.length * 40)}>
                <BarChart data={stockHist.porPorta} layout="vertical" margin={{ top: 4, right: 16, left: 8, bottom: 0 }}>
                  <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke={GRID_STROKE} />
                  <XAxis type="number" allowDecimals={false} tick={AXIS_TICK} tickLine={false} axisLine={false} />
                  <YAxis type="category" dataKey="clave" width={150} tick={AXIS_TICK} tickLine={false} axisLine={false} />
                  <Tooltip {...tooltipStyle} cursor={{ fill: '#e2e6e0' }} />
                  <Bar dataKey="cantidad" fill={BAR_FILL} radius={[0, 6, 6, 0]} maxBarSize={26} />
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <EmptyState>Sin stock a esa fecha</EmptyState>
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
