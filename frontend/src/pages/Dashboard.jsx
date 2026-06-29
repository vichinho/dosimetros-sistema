import { useEffect, useState } from 'react'
import {
  ResponsiveContainer,
  LineChart,
  Line,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Cell,
} from 'recharts'
import { getKpis } from '../api/endpoints'
import { Card, Loading, Alert, EmptyState } from '../components/ui'
import { useToast } from '../components/Toast'

const BAR_COLORS = ['#6f8f72', '#f2a65a', '#bfc6c4', '#3a4a3c', '#9bb09d', '#e0b487']
const AXIS_TICK = { fontSize: 12, fill: '#3a4a3c' }
const GRID_STROKE = '#d8d2c6'

function StatCard({ label, value, accent }) {
  return (
    <div className="bg-white rounded-2xl border border-mist/60 p-5">
      <p className="text-sm text-ink/60">{label}</p>
      <p className="text-3xl font-bold mt-1 text-ink">{value}</p>
      <div className={`mt-3 h-1 w-10 rounded-full ${accent}`} />
    </div>
  )
}

const tooltipStyle = {
  contentStyle: {
    borderRadius: 12,
    border: '1px solid #bfc6c4',
    fontSize: 13,
    boxShadow: '0 4px 12px rgba(0,0,0,0.06)',
  },
}

function BarPanel({ title, data, dataKey = 'nombre' }) {
  return (
    <Card title={title}>
      {data && data.length > 0 ? (
        <ResponsiveContainer width="100%" height={260}>
          <BarChart data={data} margin={{ top: 8, right: 8, left: -16, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke={GRID_STROKE} />
            <XAxis dataKey={dataKey} tick={AXIS_TICK} tickLine={false} axisLine={false} />
            <YAxis allowDecimals={false} tick={AXIS_TICK} tickLine={false} axisLine={false} />
            <Tooltip {...tooltipStyle} cursor={{ fill: '#ded8cc' }} />
            <Bar dataKey="cantidad" radius={[6, 6, 0, 0]} maxBarSize={56}>
              {data.map((_, i) => (
                <Cell key={i} fill={BAR_COLORS[i % BAR_COLORS.length]} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      ) : (
        <EmptyState>Sin datos</EmptyState>
      )}
    </Card>
  )
}

export default function Dashboard() {
  const [kpis, setKpis] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const toast = useToast()

  useEffect(() => {
    getKpis()
      .then(setKpis)
      .catch(() => {
        setError('No se pudieron cargar los KPIs')
        toast.error('No se pudieron cargar los KPIs')
      })
      .finally(() => setLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const estado = (clave) =>
    kpis?.dosimetrosPorEstado?.find((e) => e.clave === clave)?.cantidad ?? 0

  if (loading) return <Loading label="Cargando dashboard…" />

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-ink">Dashboard</h1>
        <p className="text-sm text-slate-500 mt-0.5">Resumen general del stock y asignaciones</p>
      </div>

      <Alert type="error">{error}</Alert>

      {kpis && (
        <>
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <StatCard label="Total dosímetros" value={kpis.totalDosimetros} accent="bg-ink" />
            <StatCard label="Disponibles" value={estado('disponible')} accent="bg-steel" />
            <StatCard label="Asignados" value={estado('asignado')} accent="bg-sun" />
            <StatCard label="Total asignaciones" value={kpis.totalAsignaciones} accent="bg-mist" />
          </div>

          <Card title="Asignaciones por trimestre">
            {kpis.asignacionesPorTrimestre?.length > 0 ? (
              <ResponsiveContainer width="100%" height={300}>
                <LineChart
                  data={kpis.asignacionesPorTrimestre}
                  margin={{ top: 8, right: 16, left: -16, bottom: 0 }}
                >
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke={GRID_STROKE} />
                  <XAxis dataKey="clave" tick={AXIS_TICK} tickLine={false} axisLine={false} />
                  <YAxis allowDecimals={false} tick={AXIS_TICK} tickLine={false} axisLine={false} />
                  <Tooltip {...tooltipStyle} />
                  <Line
                    type="monotone"
                    dataKey="cantidad"
                    stroke="#6f8f72"
                    strokeWidth={2.5}
                    dot={{ r: 4, fill: '#6f8f72' }}
                    activeDot={{ r: 6 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            ) : (
              <EmptyState>Aún no hay asignaciones registradas</EmptyState>
            )}
          </Card>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <BarPanel title="Dosímetros por tipo" data={kpis.dosimetrosPorTipo} />
            <BarPanel title="Asignaciones por empresa" data={kpis.asignacionesPorEmpresa} />
            <BarPanel title="Asignaciones por ejecutivo" data={kpis.asignacionesPorEjecutivo} />
          </div>
        </>
      )}
    </div>
  )
}
