import { useEffect, useState } from 'react'
import { getDuplicados, marcarStockEmergencia } from '../api/endpoints'
import { useAuth } from '../auth/AuthContext'
import { Card, Alert, Badge } from '../components/ui'

export default function Duplicados() {
  const { rol } = useAuth()
  const [grupos, setGrupos] = useState([])
  const [error, setError] = useState('')

  const cargar = () => getDuplicados().then(setGrupos).catch(() => setError('No se pudieron cargar los duplicados'))

  useEffect(() => {
    cargar()
  }, [])

  const marcar = async (id) => {
    await marcarStockEmergencia(id, 'stock emergencia')
    cargar()
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-ink">Dosímetros duplicados</h1>
      <p className="text-sm text-slate-500">
        Números físicos repetidos. El id interno siempre es único; los duplicados pueden marcarse como
        stock de emergencia.
      </p>
      <Alert type="error">{error}</Alert>

      {grupos.length === 0 && (
        <Card>
          <p className="text-slate-400 text-sm">No hay dosímetros con número duplicado.</p>
        </Card>
      )}

      {grupos.map((g) => (
        <Card key={g.numero} title={`Número ${g.numero} — ${g.cantidad} repetidos`}>
          <table className="w-full text-sm">
            <thead>
              <tr className="text-left text-slate-500 border-b border-slate-200">
                <th className="py-2">id interno</th>
                <th className="py-2">Tipo</th>
                <th className="py-2">Estado</th>
                <th className="py-2">Observación</th>
                {rol === 'ADMIN' && <th className="py-2"></th>}
              </tr>
            </thead>
            <tbody>
              {g.dosimetros.map((d) => (
                <tr key={d.id} className="border-b border-slate-100">
                  <td className="py-2 font-medium text-ink">{d.id}</td>
                  <td className="py-2">{d.tipoDosimetroNombre}</td>
                  <td className="py-2">{d.estado}</td>
                  <td className="py-2">
                    {d.observacion ? <Badge color="amber">{d.observacion}</Badge> : '—'}
                  </td>
                  {rol === 'ADMIN' && (
                    <td className="py-2 text-right">
                      <button onClick={() => marcar(d.id)} className="text-steel hover:underline text-sm">
                        Marcar emergencia
                      </button>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      ))}
    </div>
  )
}
