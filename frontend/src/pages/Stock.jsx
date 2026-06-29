import { useEffect, useState } from 'react'
import { getStock, getTiposDosimetro, getTiposPorta } from '../api/endpoints'
import { Card, Select, Badge, Loading, EmptyState } from '../components/ui'
import { useToast } from '../components/Toast'

const estadoColor = { disponible: 'green', asignado: 'blue', baja: 'red' }

export default function Stock() {
  const [tipos, setTipos] = useState([])
  const [portas, setPortas] = useState([])
  const [filtros, setFiltros] = useState({ tipoDosimetroId: '', tipoPortaId: '', estado: 'disponible' })
  const [dosimetros, setDosimetros] = useState([])
  const [loading, setLoading] = useState(true)
  const toast = useToast()

  useEffect(() => {
    getTiposDosimetro().then(setTipos).catch(() => {})
    getTiposPorta().then(setPortas).catch(() => {})
  }, [])

  useEffect(() => {
    const params = {}
    if (filtros.tipoDosimetroId) params.tipoDosimetroId = filtros.tipoDosimetroId
    if (filtros.tipoPortaId) params.tipoPortaId = filtros.tipoPortaId
    if (filtros.estado) params.estado = filtros.estado
    setLoading(true)
    getStock(params)
      .then(setDosimetros)
      .catch(() => toast.error('No se pudo cargar el stock'))
      .finally(() => setLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filtros])

  const portasFiltradas = filtros.tipoDosimetroId
    ? portas.filter((p) => String(p.tipoDosimetroId) === String(filtros.tipoDosimetroId))
    : portas

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-ink">Stock de dosímetros</h1>

      <Card>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Select
            label="Tipo de dosímetro"
            value={filtros.tipoDosimetroId}
            onChange={(e) => setFiltros({ ...filtros, tipoDosimetroId: e.target.value, tipoPortaId: '' })}
          >
            <option value="">Todos</option>
            {tipos.map((t) => (
              <option key={t.id} value={t.id}>{t.nombre}</option>
            ))}
          </Select>

          <Select
            label="Estado de armado (porta)"
            value={filtros.tipoPortaId}
            onChange={(e) => setFiltros({ ...filtros, tipoPortaId: e.target.value })}
          >
            <option value="">Todos</option>
            {portasFiltradas.map((p) => (
              <option key={p.id} value={p.id}>{p.nombre}</option>
            ))}
          </Select>

          <Select
            label="Estado"
            value={filtros.estado}
            onChange={(e) => setFiltros({ ...filtros, estado: e.target.value })}
          >
            <option value="disponible">Disponible</option>
            <option value="asignado">Asignado</option>
            <option value="baja">Baja</option>
            <option value="">Todos</option>
          </Select>
        </div>
      </Card>

      <Card title={`Resultados (${dosimetros.length})`}>
        {loading ? (
          <Loading />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-slate-500 border-b border-slate-200">
                  <th className="py-2 font-medium">Número</th>
                  <th className="py-2 font-medium">Tipo</th>
                  <th className="py-2 font-medium">Porta</th>
                  <th className="py-2 font-medium">Tarea</th>
                  <th className="py-2 font-medium">Bandeja/Slot</th>
                  <th className="py-2 font-medium">Estado</th>
                </tr>
              </thead>
              <tbody>
                {dosimetros.map((d) => (
                  <tr key={d.id} className="border-b border-slate-100">
                    <td className="py-2.5 font-medium text-ink">{d.numero}</td>
                    <td className="py-2.5 text-slate-600">{d.tipoDosimetroNombre}</td>
                    <td className="py-2.5 text-slate-600">{d.tipoPortaNombre || <span className="text-slate-400">—</span>}</td>
                    <td className="py-2.5 text-slate-600">{d.numeroTarea || <span className="text-slate-400">—</span>}</td>
                    <td className="py-2.5 text-slate-600">
                      {d.numeroBandeja != null ? `${d.numeroBandeja} / ${d.slotBandeja}` : '—'}
                    </td>
                    <td className="py-2.5">
                      <Badge color={estadoColor[d.estado] || 'slate'}>{d.estado}</Badge>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {dosimetros.length === 0 && <EmptyState>No hay dosímetros con esos filtros</EmptyState>}
          </div>
        )}
      </Card>
    </div>
  )
}
