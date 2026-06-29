import { useState } from 'react'
import { buscarDosimetro } from '../api/endpoints'
import { Card, Button, Input, Alert, Badge } from '../components/ui'

const estadoColor = { disponible: 'green', asignado: 'blue', baja: 'red' }

export default function Buscar() {
  const [numero, setNumero] = useState('')
  const [resultados, setResultados] = useState(null)
  const [error, setError] = useState('')

  const handleBuscar = async (e) => {
    e.preventDefault()
    setError('')
    setResultados(null)
    try {
      const data = await buscarDosimetro(numero)
      setResultados(data)
    } catch (err) {
      setError(err.response?.status === 404 ? 'No se encontraron dosímetros con ese número' : 'Error al buscar')
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-ink">Buscar dosímetro</h1>

      <Card>
        <form onSubmit={handleBuscar} className="flex gap-3 items-end">
          <Input
            label="Número físico"
            type="number"
            value={numero}
            onChange={(e) => setNumero(e.target.value)}
            required
          />
          <Button type="submit">Buscar</Button>
        </form>
      </Card>

      <Alert type="error">{error}</Alert>

      {resultados?.map((d) => (
        <Card key={d.id} title={`Dosímetro #${d.numero} (id interno ${d.id})`}>
          <div className="flex flex-wrap gap-4 text-sm mb-4">
            <span>Tipo: <b>{d.tipoDosimetroNombre}</b></span>
            <span>Porta: <b>{d.tipoPortaNombre || '—'}</b></span>
            <span>Tarea: <b>{d.numeroTarea || '—'}</b></span>
            <span>Bandeja/Slot: <b>{d.numeroBandeja != null ? `${d.numeroBandeja}/${d.slotBandeja}` : '—'}</b></span>
            <Badge color={estadoColor[d.estado] || 'slate'}>{d.estado}</Badge>
            {d.observacion && <Badge color="amber">{d.observacion}</Badge>}
          </div>

          <h3 className="text-sm font-semibold text-slate-700 mb-2">
            Historial de asignaciones ({d.asignaciones.length})
          </h3>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-slate-500 border-b border-slate-200">
                  <th className="py-2">Fecha</th>
                  <th className="py-2">Trimestre</th>
                  <th className="py-2">Cliente</th>
                  <th className="py-2">Ejecutivo</th>
                  <th className="py-2">Empresa</th>
                  <th className="py-2">Tarea (en su momento)</th>
                </tr>
              </thead>
              <tbody>
                {d.asignaciones.map((a) => (
                  <tr key={a.id} className="border-b border-slate-100">
                    <td className="py-2">{a.fechaAsignacion}</td>
                    <td className="py-2">{a.trimestre}</td>
                    <td className="py-2">{a.clienteNombre}</td>
                    <td className="py-2">{a.ejecutivoNombre}</td>
                    <td className="py-2">{a.empresaNombre}</td>
                    <td className="py-2">{a.numeroTarea || '—'}</td>
                  </tr>
                ))}
                {d.asignaciones.length === 0 && (
                  <tr>
                    <td colSpan="6" className="py-4 text-center text-slate-400">
                      Sin asignaciones registradas
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </Card>
      ))}
    </div>
  )
}
