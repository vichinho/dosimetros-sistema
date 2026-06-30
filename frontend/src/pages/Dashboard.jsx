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
import { getKpis } from '../api/endpoints'
import { Card, Loading, Alert, EmptyState, Select } from '../components/ui'
import { useToast } from '../components/Toast'

const PALETTE = ['#6f8f72', '#f2a65a', '#bfc6c4', '#3a4a3c', '#9bb09d', '#e0b487', '#8aa0ad', '#c98b54']
const ESTADO_COLOR = {
  disponible: '#6f8f72',
  asignado: '#3a4a3c',
  dañado: '#f2a65a',
  baja: '#bfc6c4',
}
const AXIS_TICK = { fontSize: 12, fill: '#3a4a3c' }
const GRID_STROKE = '#d8d2c6'
const tooltipStyle = {
  contentStyle: {
    borderRadius: 12,
    border: '1px solid #bfc6c4',
    fontSize: 13,
    boxShadow: '0 4px 12px rgba(0,0,0,0.06)',
  },
}

function StatCard({ label, value, accent }) {
  return (
    <div className="bg-white rounded-2xl border border-mist/60 p-5">
      <p className="text-sm text-ink/60">{label}</p>
      <p className="text-3xl font-bold mt-1 text-ink">{value}</p>
      <div className={`mt-3 h-1 w-10 rounded-full ${accent}`} />
    </div>
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
      <ResponsiveContainer width="100%" height={Math.max(240, horizontal ? data.length * 38 : 260)}>
        {horizontal ? (
          <BarChart data={data} layout="vertical" margin={{ top: 4, right: 16, left: 8, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke={GRID_STROKE} />
            <XAxis type="number" allowDecimals={false} tick={AXIS_TICK} tickLine={false} axisLine={false} />
            <YAxis type="category" dataKey={dataKey} width={140} tick={AXIS_TICK} tickLine={false} axisLine={false} />
            <Tooltip {...tooltipStyle} cursor={{ fill: '#f5f4e6' }} />
            <Bar dataKey="cantidad" radius={[0, 6, 6, 0]} maxBarSize={26}>
              {data.map((_, i) => (
                <Cell key={i} fill={PALETTE[i % PALETTE.length]} />
              ))}
            </Bar>
          </BarChart>
        ) : (
          <BarChart data={data} margin={{ top: 8, right: 8, left: -16, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke={GRID_STROKE} />
            <XAxis dataKey={dataKey} tick={AXIS_TICK} tickLine={false} axisLine={false} />
            <YAxis allowDecimals={false} tick={AXIS_TICK} tickLine={false} axisLine={false} />
            <Tooltip {...tooltipStyle} cursor={{ fill: '#f5f4e6' }} />
            <Bar dataKey="cantidad" radius={[6, 6, 0, 0]} maxBarSize={56}>
              {data.map((_, i) => (
                <Cell key={i} fill={PALETTE[i % PALETTE.length]} />
              ))}
            </Bar>
          </BarChart>
        )}
      </ResponsiveContainer>
    </Card>
  )
}

export default function Dashboard() {
  const [kpis, setKpis] = useState(null)
  const [trimestre, setTrimestre] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
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

  if (loading && !kpis) return <Loading label="Cargando dashboard…" />

  const sufijo = trimestre ? ` · ${trimestre}` : ''

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-ink">Dashboard</h1>
          <p className="text-sm text-slate-500 mt-0.5">Resumen de stock y asignaciones</p>
        </div>
        <div className="w-full sm:w-56">
          <Select
            label="Filtrar asignaciones por trimestre"
            value={trimestre}
            onChange={(e) => setTrimestre(e.target.value)}
          >
            <option value="">Todos los trimestres</option>
            {kpis?.trimestresDisponibles?.map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </Select>
        </div>
      </div>

      <Alert type="error">{error}</Alert>

      {kpis && (
        <>
          {/* KPIs de stock */}
          <div className="grid grid-cols-2 lg:grid-cols-5 gap-4">
            <StatCard label="Total dosímetros" value={kpis.totalDosimetros} accent="bg-ink" />
            <StatCard label="Disponibles para asignar" value={kpis.disponibles} accent="bg-steel" />
            <StatCard label="Asignados" value={kpis.asignados} accent="bg-[#3a4a3c]" />
            <StatCard label="Dañados" value={kpis.danados} accent="bg-sun" />
            <StatCard label={`Asignaciones${sufijo}`} value={kpis.totalAsignaciones} accent="bg-mist" />
          </div>

          {/* Estado (dona) + evolución por trimestre */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <Card title="Dosímetros por estado">
              {kpis.dosimetrosPorEstado?.length > 0 ? (
                <ResponsiveContainer width="100%" height={260}>
                  <PieChart>
                    <Pie
                      data={kpis.dosimetrosPorEstado}
                      dataKey="cantidad"
                      nameKey="clave"
                      innerRadius={55}
                      outerRadius={90}
                      paddingAngle={2}
                    >
                      {kpis.dosimetrosPorEstado.map((e, i) => (
                        <Cell key={i} fill={ESTADO_COLOR[e.clave] || PALETTE[i % PALETTE.length]} />
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

            <Card title="Asignaciones por trimestre" className="lg:col-span-2">
              {kpis.asignacionesPorTrimestre?.length > 0 ? (
                <ResponsiveContainer width="100%" height={260}>
                  <LineChart data={kpis.asignacionesPorTrimestre} margin={{ top: 8, right: 16, left: -16, bottom: 0 }}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke={GRID_STROKE} />
                    <XAxis dataKey="clave" tick={AXIS_TICK} tickLine={false} axisLine={false} />
                    <YAxis allowDecimals={false} tick={AXIS_TICK} tickLine={false} axisLine={false} />
                    <Tooltip {...tooltipStyle} />
                    <Line type="monotone" dataKey="cantidad" stroke="#6f8f72" strokeWidth={2.5} dot={{ r: 4, fill: '#6f8f72' }} activeDot={{ r: 6 }} />
                  </LineChart>
                </ResponsiveContainer>
              ) : (
                <EmptyState>Aún no hay asignaciones</EmptyState>
              )}
            </Card>
          </div>

          {/* Desgloses */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <BarPanel title="Disponibles por porta (estado de armado)" data={kpis.disponiblesPorPorta} />
            <BarPanel title="Dosímetros por tipo" data={kpis.dosimetrosPorTipo} />
            <BarPanel title={`Asignaciones por empresa${sufijo}`} data={kpis.asignacionesPorEmpresa} />
            <BarPanel title={`Asignaciones por tipo de porta${sufijo}`} data={kpis.asignacionesPorTipoPorta} />
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <BarPanel title={`Asignaciones por ejecutivo${sufijo}`} data={kpis.asignacionesPorEjecutivo} horizontal />
            <BarPanel title={`Top 5 clientes${sufijo}`} data={(kpis.topClientes || []).slice(0, 5)} horizontal />
          </div>
        </>
      )}
    </div>
  )
}
