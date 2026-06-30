import { useCallback, useEffect, useState } from 'react'
import {
  getStock,
  getTiposDosimetro,
  getTiposPorta,
  getPortasDisponibles,
  exportarStockExcel,
  marcarDanado,
  marcarBueno,
} from '../api/endpoints'
import { Card, Select, Badge, Button, Loading, EmptyState } from '../components/ui'
import { useToast } from '../components/Toast'

const estadoColor = { disponible: 'green', asignado: 'blue', baja: 'red', dañado: 'amber' }

export default function Stock() {
  const [tipos, setTipos] = useState([])
  const [portas, setPortas] = useState([])
  const [filtros, setFiltros] = useState({ tipoDosimetroId: '', tipoPortaId: '', estado: 'disponible' })
  const [dosimetros, setDosimetros] = useState([])
  const [detallePortas, setDetallePortas] = useState([])
  const [loading, setLoading] = useState(true)
  const toast = useToast()

  useEffect(() => {
    getTiposDosimetro().then(setTipos).catch(() => {})
    getTiposPorta().then(setPortas).catch(() => {})
    getPortasDisponibles().then(setDetallePortas).catch(() => {})
  }, [])

  const construirParams = useCallback(() => {
    const params = {}
    if (filtros.tipoDosimetroId) params.tipoDosimetroId = filtros.tipoDosimetroId
    if (filtros.tipoPortaId) params.tipoPortaId = filtros.tipoPortaId
    if (filtros.estado) params.estado = filtros.estado
    return params
  }, [filtros])

  const cargar = useCallback(() => {
    setLoading(true)
    getStock(construirParams())
      .then(setDosimetros)
      .catch(() => toast.error('No se pudo cargar el stock'))
      .finally(() => setLoading(false))
  }, [construirParams, toast])

  useEffect(() => {
    cargar()
  }, [cargar])

  const onExportar = async () => {
    try {
      const blob = await exportarStockExcel(construirParams())
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'stock.xlsx'
      a.click()
      URL.revokeObjectURL(url)
    } catch {
      toast.error('No se pudo exportar el stock')
    }
  }

  const recargarDetalle = () => getPortasDisponibles().then(setDetallePortas).catch(() => {})

  const onMarcarDanado = async (id) => {
    try {
      await marcarDanado(id)
      toast.success('Dosímetro marcado como dañado')
      cargar()
      recargarDetalle()
    } catch (err) {
      toast.error(err.response?.data?.message || 'No se pudo marcar como dañado')
    }
  }

  const onMarcarBueno = async (id) => {
    try {
      await marcarBueno(id)
      toast.success('Dosímetro marcado como bueno (disponible)')
      cargar()
      recargarDetalle()
    } catch (err) {
      toast.error(err.response?.data?.message || 'No se pudo marcar como bueno')
    }
  }

  const portasFiltradas = filtros.tipoDosimetroId
    ? portas.filter((p) => String(p.tipoDosimetroId) === String(filtros.tipoDosimetroId))
    : portas

  const totalDisponibles = detallePortas.reduce((acc, p) => acc + (p.cantidad || 0), 0)

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-ink">Stock de dosímetros</h1>

      {/* Detalle de portas disponibles */}
      <Card title={`Portas disponibles · ${totalDisponibles} en total`}>
        {detallePortas.length === 0 ? (
          <EmptyState>No hay dosímetros disponibles</EmptyState>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3">
            {detallePortas.map((p) => (
              <div key={p.tipoPortaId} className="border border-mist/60 rounded-xl p-3">
                <p className="text-2xl font-bold text-ink">{p.cantidad}</p>
                <p className="text-sm font-medium text-ink/80">{p.portaNombre}</p>
                <p className="text-xs text-slate-500">{p.tipoDosimetroNombre}</p>
              </div>
            ))}
          </div>
        )}
      </Card>

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
            <option value="dañado">Dañado</option>
            <option value="baja">Baja</option>
            <option value="">Todos</option>
          </Select>
        </div>
      </Card>

      <Card
        title={`Resultados (${dosimetros.length})`}
        action={
          <Button variant="secondary" onClick={onExportar} disabled={dosimetros.length === 0}>
            Exportar a Excel
          </Button>
        }
      >
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
                  <th className="py-2 font-medium text-right">Acciones</th>
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
                    <td className="py-2.5 text-right">
                      {d.estado === 'disponible' && (
                        <button onClick={() => onMarcarDanado(d.id)} className="text-amber-600 hover:underline text-sm">
                          Marcar dañado
                        </button>
                      )}
                      {d.estado === 'dañado' && (
                        <button onClick={() => onMarcarBueno(d.id)} className="text-steel hover:underline text-sm">
                          Marcar bueno
                        </button>
                      )}
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
