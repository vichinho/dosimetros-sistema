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

const BAR_COLORS = ['#2563eb', '#7c3aed', '#0891b2', '#059669', '#d97706', '#dc2626']

function StatCard({ label, value, accent }) {
  return (
    <div className="bg-white rounded-2xl border border-slate-200/70 p-5">
      <p className="text-sm text-slate-500">{label}</p>
      <p className="text-3xl font-bold mt-1 text-slate-800">{value}</p>
      <div className={`mt-3 h-1 w-10 rounded-full ${accent}`} />
    </div>
  )
}

const tooltipStyle = {
  contentStyle: {
    borderRadius: 12,
    border: '1px solid #e2e8f0',
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
            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
            <XAxis dataKey={dataKey} tick={{ fontSize: 12, fill: '#64748b' }} tickLine={false} axisLine={false} />
            <YAxis allowDecimals={false} tick={{ fontSize: 12, fill: '#64748b' }} tickLine={false} axisLine={false} />
            <Tooltip {...tooltipStyle} cursor={{ fill: '#f8fafc' }} />
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
        <h1 className="text-2xl font-bold text-slate-800">Dashboard</h1>
        <p className="text-sm text-slate-500 mt-0.5">Resumen general del stock y asignaciones</p>
      </div>

      <Alert type="error">{error}</Alert>

      {kpis && (
        <>
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <StatCard label="Total dosímetros" value={kpis.totalDosimetros} accent="bg-slate-400" />
            <StatCard label="Disponibles" value={estado('disponible')} accent="bg-emerald-500" />
            <StatCard label="Asignados" value={estado('asignado')} accent="bg-blue-500" />
            <StatCard label="Total asignaciones" value={kpis.totalAsignaciones} accent="bg-violet-500" />
          </div>

          <Card title="Asignaciones por trimestre">
            {kpis.asignacionesPorTrimestre?.length > 0 ? (
              <ResponsiveContainer width="100%" height={300}>
                <LineChart
                  data={kpis.asignacionesPorTrimestre}
                  margin={{ top: 8, right: 16, left: -16, bottom: 0 }}
                >
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                  <XAxis dataKey="clave" tick={{ fontSize: 12, fill: '#64748b' }} tickLine={false} axisLine={false} />
                  <YAxis allowDecimals={false} tick={{ fontSize: 12, fill: '#64748b' }} tickLine={false} axisLine={false} />
                  <Tooltip {...tooltipStyle} />
                  <Line
                    type="monotone"
                    dataKey="cantidad"
                    stroke="#2563eb"
                    strokeWidth={2.5}
                    dot={{ r: 4, fill: '#2563eb' }}
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
