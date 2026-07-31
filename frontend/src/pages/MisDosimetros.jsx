import { useEffect, useState } from 'react'
import { getMisAsignaciones, getMisFiltros } from '../api/endpoints'
import { Card, Select, Alert, Loading, EmptyState } from '../components/ui'

// Ordena trimestres 'QTYYYY' del más reciente al más antiguo.
function ordenarTrimestres(lista) {
  return [...lista].sort((a, b) => {
    const ay = a.slice(2), by = b.slice(2)
    if (ay !== by) return by.localeCompare(ay)
    return b.slice(0, 1).localeCompare(a.slice(0, 1))
  })
}

// Agrupa las asignaciones por trimestre + fecha de asignación (lotes).
function agruparLotes(asignaciones) {
  const map = new Map()
  for (const a of asignaciones) {
    const clave = `${a.trimestre}|${a.fechaAsignacion}`
    if (!map.has(clave)) {
      map.set(clave, { trimestre: a.trimestre, fechaAsignacion: a.fechaAsignacion, asignaciones: [] })
    }
    map.get(clave).asignaciones.push(a)
  }
  return [...map.values()].map((l) => ({ ...l, cantidad: l.asignaciones.length }))
}

export default function MisDosimetros() {
  const [trimestres, setTrimestres] = useState([])
  const [trimestre, setTrimestre] = useState('')
  const [lotes, setLotes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const cargar = (t) => {
    if (!t) {
      setLotes([])
      setLoading(false)
      return
    }
    setLoading(true)
    setError('')
    getMisAsignaciones({ trimestre: t })
      .then((asigs) => setLotes(agruparLotes(asigs)))
      .catch((err) =>
        setError(
          err.response?.status === 409
            ? 'Tu usuario no tiene un ejecutivo asociado. Contacta a un administrador.'
            : 'No se pudieron cargar tus dosímetros'
        )
      )
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    getMisFiltros()
      .then((op) => {
        const ts = ordenarTrimestres(op.trimestres || [])
        setTrimestres(ts)
        // Carga inicial acotada al trimestre más reciente (no todo el histórico).
        if (ts[0]) {
          setTrimestre(ts[0])
          cargar(ts[0])
        } else {
          setLoading(false)
        }
      })
      .catch((err) => {
        setError(
          err.response?.status === 409
            ? 'Tu usuario no tiene un ejecutivo asociado. Contacta a un administrador.'
            : 'No se pudieron cargar tus dosímetros'
        )
        setLoading(false)
      })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const onTrimestre = (e) => {
    const t = e.target.value
    setTrimestre(t)
    cargar(t)
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-ink">Mis dosímetros (lotes enviados)</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Agrupados por fecha de asignación. Elige un <b>trimestre</b> para ver sus lotes.
        </p>
      </div>

      {error && <Alert type="error">{error}</Alert>}

      <Card title="Trimestre">
        <div className="max-w-xs">
          <Select label="Trimestre" value={trimestre} onChange={onTrimestre}>
            <option value="">Selecciona…</option>
            {trimestres.map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </Select>
        </div>
      </Card>

      {loading ? (
        <Loading />
      ) : !trimestre ? (
        <Card><EmptyState>Selecciona un trimestre para ver tus lotes.</EmptyState></Card>
      ) : lotes.length === 0 ? (
        <Card><EmptyState>No tienes dosímetros asignados en {trimestre}.</EmptyState></Card>
      ) : (
        lotes.map((lote, i) => (
          <Card key={i} title={`${lote.trimestre} — ${lote.fechaAsignacion} (${lote.cantidad} dosímetros)`}>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-slate-500 border-b border-slate-200">
                    <th className="py-2">N° dosímetro</th>
                    <th className="py-2">Cliente</th>
                    <th className="py-2">Empresa</th>
                    <th className="py-2">Porta</th>
                    <th className="py-2">Trello</th>
                  </tr>
                </thead>
                <tbody>
                  {lote.asignaciones.map((a) => (
                    <tr key={a.id} className="border-b border-slate-100">
                      <td className="py-2 font-medium text-ink">{a.numeroDosimetro}</td>
                      <td className="py-2">{a.clienteNombre}</td>
                      <td className="py-2">{a.empresaNombre}</td>
                      <td className="py-2">{a.tipoPortaNombre}</td>
                      <td className="py-2">
                        {a.linkTrello ? (
                          <a href={a.linkTrello} target="_blank" rel="noreferrer" className="text-steel hover:underline">
                            Ver
                          </a>
                        ) : (
                          '—'
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>
        ))
      )}
    </div>
  )
}
