import { useEffect, useState } from 'react'
import { getKpis } from '../api/endpoints'
import { Card, Alert } from '../components/ui'

function StatCard({ label, value, color = 'text-slate-800' }) {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
      <p className="text-sm text-slate-500">{label}</p>
      <p className={`text-3xl font-bold mt-1 ${color}`}>{value}</p>
    </div>
  )
}

function ConteoTabla({ filas, columnaClave }) {
  if (!filas || filas.length === 0) {
    return <p className="text-sm text-slate-400">Sin datos</p>
  }
  return (
    <table className="w-full text-sm">
      <tbody>
        {filas.map((f, i) => (
          <tr key={i} className="border-b border-slate-100 last:border-0">
            <td className="py-2 text-slate-700">{f[columnaClave]}</td>
            <td className="py-2 text-right font-semibold text-slate-800">{f.cantidad}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

export default function Dashboard() {
  const [kpis, setKpis] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    getKpis()
      .then(setKpis)
      .catch(() => setError('No se pudieron cargar los KPIs'))
  }, [])

  const estado = (clave) =>
    kpis?.dosimetrosPorEstado?.find((e) => e.clave === clave)?.cantidad ?? 0

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-800">Dashboard</h1>
      <Alert type="error">{error}</Alert>

      {kpis && (
        <>
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <StatCard label="Total dosímetros" value={kpis.totalDosimetros} />
            <StatCard label="Disponibles" value={estado('disponible')} color="text-green-600" />
            <StatCard label="Asignados" value={estado('asignado')} color="text-blue-600" />
            <StatCard label="Total asignaciones" value={kpis.totalAsignaciones} />
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card title="Dosímetros por tipo">
              <ConteoTabla filas={kpis.dosimetrosPorTipo} columnaClave="nombre" />
            </Card>
            <Card title="Asignaciones por empresa">
              <ConteoTabla filas={kpis.asignacionesPorEmpresa} columnaClave="nombre" />
            </Card>
            <Card title="Asignaciones por ejecutivo">
              <ConteoTabla filas={kpis.asignacionesPorEjecutivo} columnaClave="nombre" />
            </Card>
            <Card title="Asignaciones por trimestre">
              <ConteoTabla filas={kpis.asignacionesPorTrimestre} columnaClave="clave" />
            </Card>
          </div>
        </>
      )}
    </div>
  )
}
