import { useEffect, useState } from 'react'
import { getMisAsignaciones, getMisFiltros } from '../api/endpoints'
import { Card, Select, Input, Button, Alert, Loading, EmptyState, Pagination } from '../components/ui'
import Combobox from '../components/Combobox'
import { useToast } from '../components/Toast'

const POR_PAGINA = 25

const VACIO = { clienteId: '', trimestre: '', fecha: '' }

// Ordena trimestres 'QTYYYY' del más reciente al más antiguo.
function ordenarTrimestres(lista) {
  return [...lista].sort((a, b) => {
    const ay = a.slice(2), by = b.slice(2)
    if (ay !== by) return by.localeCompare(ay)
    return b.slice(0, 1).localeCompare(a.slice(0, 1))
  })
}

// Orden solicitado: fecha de asignación, tarea, bandeja, slot.
function comparar(a, b) {
  const fa = a.fechaAsignacion || ''
  const fb = b.fechaAsignacion || ''
  if (fa !== fb) return fa.localeCompare(fb)
  const ta = Number(a.numeroTarea) || 0
  const tb = Number(b.numeroTarea) || 0
  if (ta !== tb) return ta - tb
  const ba = a.numeroBandeja ?? -1
  const bb = b.numeroBandeja ?? -1
  if (ba !== bb) return ba - bb
  return (a.slotBandeja ?? -1) - (b.slotBandeja ?? -1)
}

export default function MisDosimetros() {
  const [opciones, setOpciones] = useState({ trimestres: [], clientes: [], portas: [] })
  const [filtros, setFiltros] = useState(VACIO)
  const [asignaciones, setAsignaciones] = useState([])
  const [loading, setLoading] = useState(false)
  const [buscado, setBuscado] = useState(false)
  const [page, setPage] = useState(1)
  const [error, setError] = useState('')
  const [exportando, setExportando] = useState(false)
  const toast = useToast()

  const set = (campo) => (e) => setFiltros({ ...filtros, [campo]: e.target.value })

  // Se exige al menos un filtro: no hay vista general.
  const tieneFiltro = (f) => Boolean(f.clienteId || f.trimestre || f.fecha)

  const paramsActivos = (f = filtros) => {
    const params = {}
    Object.entries(f).forEach(([k, v]) => {
      if (v !== '' && v != null) params[k] = v
    })
    return params
  }

  useEffect(() => {
    getMisFiltros()
      .then(setOpciones)
      .catch((err) =>
        setError(
          err.response?.status === 409
            ? 'Tu usuario no tiene un ejecutivo asociado. Contacta a un administrador.'
            : 'No se pudieron cargar los filtros'
        )
      )
  }, [])

  const cargar = (f = filtros) => {
    if (!tieneFiltro(f)) {
      setAsignaciones([])
      setBuscado(false)
      return
    }
    setLoading(true)
    setError('')
    getMisAsignaciones(paramsActivos(f))
      .then((data) => {
        setAsignaciones([...data].sort(comparar))
        setBuscado(true)
        setPage(1)
      })
      .catch((err) =>
        setError(
          err.response?.status === 409
            ? 'Tu usuario no tiene un ejecutivo asociado. Contacta a un administrador.'
            : 'No se pudieron cargar tus dosímetros'
        )
      )
      .finally(() => setLoading(false))
  }

  const aplicar = (e) => {
    e.preventDefault()
    if (!tieneFiltro(filtros)) {
      toast.error('Aplica al menos un filtro (cliente, trimestre o fecha).')
      return
    }
    cargar()
  }

  const limpiar = () => {
    setFiltros(VACIO)
    setAsignaciones([])
    setBuscado(false)
  }

  // Exporta las asignaciones filtradas, ya ordenadas por fecha, tarea, bandeja y slot.
  const exportar = () => {
    if (!asignaciones.length) {
      toast.error('No hay datos para exportar.')
      return
    }
    setExportando(true)
    try {
      const sep = ';'
      const esc = (v) => {
        const s = String(v ?? '')
        return /[";\n\r]/.test(s) ? '"' + s.replace(/"/g, '""') + '"' : s
      }
      const enc = ['N° dosímetro', 'Cliente', 'Empresa', 'Trimestre', 'Fecha asignación',
        'Tipo porta', 'Tarea', 'Bandeja', 'Slot', 'Trello']
      const filas = asignaciones.map((a) => [
        a.numeroDosimetro, a.clienteNombre, a.empresaNombre, a.trimestre, a.fechaAsignacion,
        a.tipoPortaNombre, a.numeroTarea || '', a.numeroBandeja ?? '', a.slotBandeja ?? '',
        a.linkTrello || '',
      ])
      const csv = '﻿' + [enc, ...filas].map((r) => r.map(esc).join(sep)).join('\r\n')
      const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `mis-dosimetros_${new Date().toISOString().slice(0, 10)}.csv`
      a.click()
      URL.revokeObjectURL(url)
      toast.success(`Exportados ${asignaciones.length} dosímetros`)
    } finally {
      setExportando(false)
    }
  }

  const totalPages = Math.ceil(asignaciones.length / POR_PAGINA)
  const visibles = asignaciones.slice((page - 1) * POR_PAGINA, page * POR_PAGINA)

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-ink">Mis dosímetros</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Aplica al menos un filtro (<b>cliente</b>, <b>trimestre</b> o <b>fecha</b>) para ver tus dosímetros.
        </p>
      </div>

      {error && <Alert type="error">{error}</Alert>}

      <Card title="Filtros">
        <form onSubmit={aplicar} className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Combobox
            label="Cliente"
            options={opciones.clientes.map((c) => ({ value: c.id, label: c.nombre }))}
            value={filtros.clienteId}
            onChange={(v) => setFiltros((f) => ({ ...f, clienteId: v }))}
            placeholder="Escribe para buscar…"
          />

          <Select label="Trimestre" value={filtros.trimestre} onChange={set('trimestre')}>
            <option value="">Todos</option>
            {ordenarTrimestres(opciones.trimestres).map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </Select>
          <Input label="Fecha de asignación" type="date" value={filtros.fecha} onChange={set('fecha')} />
          <div className="flex items-end gap-2">
            <Button type="submit">Aplicar</Button>
            <Button type="button" variant="secondary" onClick={limpiar}>Limpiar</Button>
          </div>
        </form>
      </Card>

      <Card
        title={`Dosímetros (${asignaciones.length})`}
        action={
          <Button variant="secondary" onClick={exportar} disabled={exportando || asignaciones.length === 0}>
            {exportando ? 'Exportando…' : 'Exportar'}
          </Button>
        }
      >
        {loading ? (
          <Loading />
        ) : !buscado ? (
          <EmptyState>Aplica al menos un filtro y presiona Aplicar para ver tus dosímetros.</EmptyState>
        ) : asignaciones.length === 0 ? (
          <EmptyState>No hay dosímetros con esos filtros.</EmptyState>
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
