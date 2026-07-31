import { useEffect, useState } from 'react'
import { getMisAsignaciones, getMisFiltros, exportarMisAsignacionesExcel } from '../api/endpoints'
import { Card, Select, Input, Button, Loading, EmptyState, Badge, Pagination } from '../components/ui'
import { useToast } from '../components/Toast'

const POR_PAGINA = 25

const VACIO = {
  clienteId: '',
  trimestre: '',
  fecha: '',
  tipoPortaId: '',
  numeroBandeja: '',
  slotBandeja: '',
  link: '',
}

// Ordena trimestres 'QTYYYY' del más reciente al más antiguo.
function ordenarTrimestres(lista) {
  return [...lista].sort((a, b) => {
    const ay = a.slice(2), by = b.slice(2)
    if (ay !== by) return by.localeCompare(ay)
    return b.slice(0, 1).localeCompare(a.slice(0, 1))
  })
}

export default function MisAsignaciones() {
  const [opciones, setOpciones] = useState({ trimestres: [], clientes: [], portas: [] })
  const [filtros, setFiltros] = useState(VACIO)
  const [asignaciones, setAsignaciones] = useState([])
  const [loading, setLoading] = useState(true)
  const [buscado, setBuscado] = useState(false)
  const [page, setPage] = useState(1)
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

  // Para no traer TODO, se exige al menos un trimestre o un cliente.
  const tieneFiltroMinimo = (f) => Boolean(f.trimestre || f.clienteId)

  const cargar = (f = filtros) => {
    if (!tieneFiltroMinimo(f)) {
      setAsignaciones([])
      setBuscado(false)
      setLoading(false)
      return
    }
    setLoading(true)
    setError('')
    getMisAsignaciones(paramsActivos(f))
      .then((data) => {
        setAsignaciones(data)
        setBuscado(true)
        setPage(1)
      })
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
    getMisFiltros()
      .then((op) => {
        setOpciones(op)
        // Carga inicial acotada al trimestre más reciente (no todo el histórico).
        const t = ordenarTrimestres(op.trimestres || [])[0]
        if (t) {
          const f = { ...VACIO, trimestre: t }
          setFiltros(f)
          cargar(f)
        } else {
          setLoading(false)
        }
      })
      .catch(() => setLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

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

  const aplicar = (e) => {
    e.preventDefault()
    cargar()
  }

  const limpiar = () => {
    setFiltros(VACIO)
    setAsignaciones([])
    setBuscado(false)
  }

  const totalPages = Math.ceil(asignaciones.length / POR_PAGINA)
  const visibles = asignaciones.slice((page - 1) * POR_PAGINA, page * POR_PAGINA)

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-ink">Mis asignaciones</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Elige un <b>trimestre</b> o un <b>cliente</b> y presiona <b>Aplicar</b>. Por defecto se
          muestra el trimestre más reciente.
        </p>
      </div>

      {error && <Badge color="red">{error}</Badge>}

      <Card title="Filtros">
        <form onSubmit={aplicar} className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Select label="Trimestre" value={filtros.trimestre} onChange={set('trimestre')}>
            <option value="">Todos</option>
            {ordenarTrimestres(opciones.trimestres).map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </Select>
          <Select label="Cliente" value={filtros.clienteId} onChange={set('clienteId')}>
            <option value="">Todos</option>
            {opciones.clientes.map((c) => (
              <option key={c.id} value={c.id}>{c.nombre}</option>
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
        title={`Resultados (${asignaciones.length})`}
        action={
          <Button variant="secondary" onClick={exportar} disabled={exportando || asignaciones.length === 0}>
            {exportando ? 'Exportando…' : 'Exportar a Excel'}
          </Button>
        }
      >
        {loading ? (
          <Loading />
        ) : !tieneFiltroMinimo(filtros) && !buscado ? (
          <EmptyState>Selecciona al menos un trimestre o un cliente y presiona Aplicar.</EmptyState>
        ) : asignaciones.length === 0 ? (
          <EmptyState>No hay asignaciones con esos filtros.</EmptyState>
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
            <Pagination page={page} totalPages={totalPages} onChange={setPage} />
          </div>
        )}
      </Card>
    </div>
  )
}
