import { useEffect, useState } from 'react'
import { getMisLotes } from '../api/endpoints'
import { Card, Alert } from '../components/ui'

export default function MisDosimetros() {
  const [lotes, setLotes] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    getMisLotes()
      .then(setLotes)
      .catch((err) =>
        setError(
          err.response?.status === 409
            ? 'Tu usuario no tiene un ejecutivo asociado. Contacta a un administrador.'
            : 'No se pudieron cargar tus dosímetros'
        )
      )
  }, [])

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-800">Mis dosímetros (lotes enviados)</h1>
      <p className="text-sm text-slate-500">Agrupados por trimestre y fecha de asignación.</p>
      <Alert type="error">{error}</Alert>

      {lotes.length === 0 && !error && (
        <Card>
          <p className="text-slate-400 text-sm">Aún no tienes dosímetros asignados.</p>
        </Card>
      )}

      {lotes.map((lote, i) => (
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
                    <td className="py-2 font-medium text-slate-800">{a.numeroDosimetro}</td>
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
      ))}
    </div>
  )
}
