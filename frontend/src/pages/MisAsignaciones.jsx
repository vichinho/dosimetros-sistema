import { useEffect, useState } from 'react'
import { getMisAsignaciones, getMisFiltros, exportarMisAsignacionesExcel } from '../api/endpoints'
import { Card, Select, Input, Button, Loading, EmptyState, Badge } from '../components/ui'
import { useToast } from '../components/Toast'

const LIMITE = 500

const VACIO = {
  clienteId: '',
  trimestre: '',
  fecha: '',
  tipoPortaId: '',
  numeroBandeja: '',
  slotBandeja: '',
  link: '',
}

export default function MisAsignaciones() {
  const [opciones, setOpciones] = useState({ trimestres: [], clientes: [], portas: [] })
  const [filtros, setFiltros] = useState(VACIO)
  const [asignaciones, setAsignaciones] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [exportando, setExportando] = useState(false)
  const toast = useToast()

  const set = (campo) => (e) => setFiltros({ ...filtros, [campo]: e.target.value })

  const paramsActivos = (f = filtros) => {
    const params = {}
    Object.entries(f).forEach(([k, v]) => {
      if (v !== '' && v != null) params[k] = v
    })
    return params
  }

  const exportar = async () => {
    setExportando(true)
    try {
      const blob = await exportarMisAsignacionesExcel(paramsActivos())
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'mis-asignaciones.xlsx'
      a.click()
      URL.revokeObjectURL(url)
    } catch {
      toast.error('No se pudo exportar')
    } finally {
      setExportando(false)
    }
  }

  const cargar = (f = filtros) => {
    const params = {}
    Object.entries(f).forEach(([k, v]) => {
      if (v !== '' && v != null) params[k] = v
    })
    setLoading(true)
    getMisAsignaciones(params)
      .then(setAsignaciones)
      .catch((err) =>
        setError(
          err.response?.status === 409
            ? 'Tu usuario no tiene un ejecutivo asociado. Contacta a un administrador.'
            : 'No se pudieron cargar tus asignaciones'
        )
      )
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    getMisFiltros().then(setOpciones).catch(() => {})
    cargar(VACIO)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const aplicar = (e) => {
    e.preventDefault()
    cargar()
  }

  const limpiar = () => {
    setFiltros(VACIO)
    cargar(VACIO)
  }

  const visibles = asignaciones.slice(0, LIMITE)

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-ink">Mis asignaciones</h1>
        <p className="text-sm text-slate-500 mt-0.5">Filtra tus dosímetros asignados.</p>
      </div>

      {error && <Badge color="red">{error}</Badge>}

      <Card title="Filtros">
        <form onSubmit={aplicar} className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Select label="Cliente" value={filtros.clienteId} onChange={set('clienteId')}>
            <option value="">Todos</option>
            {opciones.clientes.map((c) => (
              <option key={c.id} value={c.id}>{c.nombre}</option>
            ))}
          </Select>
          <Select label="Trimestre" value={filtros.trimestre} onChange={set('trimestre')}>
            <option value="">Todos</option>
            {opciones.trimestres.map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </Select>
          <Select label="Tipo de porta" value={filtros.tipoPortaId} onChange={set('tipoPortaId')}>
            <option value="">Todos</option>
            {opciones.portas.map((p) => (
              <option key={p.id} value={p.id}>{p.nombre}</option>
            ))}
          </Select>
          <Input label="Fecha de asignación" type="date" value={filtros.fecha} onChange={set('fecha')} />
          <Input label="N° de bandeja" type="number" value={filtros.numeroBandeja} onChange={set('numeroBandeja')} />
          <Input label="Slot de bandeja" type="number" value={filtros.slotBandeja} onChange={set('slotBandeja')} />
          <Input label="Link de Trello (fecha de ingreso)" value={filtros.link} onChange={set('link')} />
          <div className="flex items-end gap-2">
            <Button type="submit">Aplicar</Button>
            <Button type="button" variant="secondary" onClick={limpiar}>Limpiar</Button>
          </div>
        </form>
      </Card>

      <Card
        title={`Resultados (${asignaciones.length}${asignaciones.length > LIMITE ? `, mostrando ${LIMITE}` : ''})`}
        action={
          <Button variant="secondary" onClick={exportar} disabled={exportando || asignaciones.length === 0}>
            {exportando ? 'Exportando…' : 'Exportar a Excel'}
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
                  <th className="py-2 font-medium">N° dosímetro</th>
                  <th className="py-2 font-medium">Cliente</th>
                  <th className="py-2 font-medium">Empresa</th>
                  <th className="py-2 font-medium">Trimestre</th>
                  <th className="py-2 font-medium">Fecha asig.</th>
                  <th className="py-2 font-medium">Porta</th>
                  <th className="py-2 font-medium">Tarea</th>
                  <th className="py-2 font-medium">Bandeja/Slot</th>
                  <th className="py-2 font-medium">Trello</th>
                </tr>
              </thead>
              <tbody>
                {visibles.map((a) => (
                  <tr key={a.id} className="border-b border-slate-100">
                    <td className="py-2.5 font-medium text-ink">{a.numeroDosimetro}</td>
                    <td className="py-2.5 text-slate-600">{a.clienteNombre}</td>
                    <td className="py-2.5 text-slate-600">{a.empresaNombre}</td>
                    <td className="py-2.5 text-slate-600">{a.trimestre}</td>
                    <td className="py-2.5 text-slate-600">{a.fechaAsignacion}</td>
                    <td className="py-2.5 text-slate-600">{a.tipoPortaNombre}</td>
                    <td className="py-2.5 text-slate-600">{a.numeroTarea || '—'}</td>
                    <td className="py-2.5 text-slate-600">
                      {a.numeroBandeja != null ? `${a.numeroBandeja} / ${a.slotBandeja}` : '—'}
                    </td>
                    <td className="py-2.5">
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
            {asignaciones.length === 0 && <EmptyState>No hay asignaciones con esos filtros</EmptyState>}
          </div>
        )}
      </Card>
    </div>
  )
}
