import { useState } from 'react'
import { buscarDosimetro } from '../api/endpoints'
import { Button, Badge, Loading } from '../components/ui'
import { IconSearch, IconBox } from '../components/icons'

const estadoColor = { disponible: 'green', asignado: 'blue', baja: 'red', dañado: 'amber' }

function Chip({ etiqueta, valor }) {
  return (
    <div className="bg-cream/60 border border-mist/60 rounded-xl px-4 py-2">
      <p className="text-[11px] uppercase tracking-wide text-ink/50">{etiqueta}</p>
      <p className="text-sm font-semibold text-ink">{valor ?? '—'}</p>
    </div>
  )
}

export default function Buscar() {
  const [numero, setNumero] = useState('')
  const [resultados, setResultados] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleBuscar = async (e) => {
    e.preventDefault()
    setError('')
    setResultados(null)
    setLoading(true)
    try {
      const data = await buscarDosimetro(numero)
      setResultados(data)
    } catch (err) {
      setError(
        err.response?.status === 404
          ? `No se encontraron dosímetros asignados con el número ${numero}`
          : 'Error al buscar'
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-ink">Buscar dosímetro</h1>

      {/* Buscador */}
      <div className="bg-white rounded-2xl border border-mist/60 p-6">
        <form onSubmit={handleBuscar} className="flex flex-col sm:flex-row gap-3 sm:items-center">
          <div className="relative flex-1">
            <span className="absolute left-3 top-1/2 -translate-y-1/2 text-ink/40">
              <IconSearch />
            </span>
            <input
              type="number"
              value={numero}
              onChange={(e) => setNumero(e.target.value)}
              placeholder="Número físico del dosímetro…"
              required
              className="w-full pl-10 pr-3 py-2.5 border border-mist rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-steel/40 focus:border-steel transition"
            />
          </div>
          <Button type="submit" className="sm:w-auto">Buscar</Button>
        </form>
      </div>

      {loading && <Loading label="Buscando…" />}

      {error && (
        <div className="bg-white rounded-2xl border border-mist/60 p-10 text-center">
          <div className="w-12 h-12 mx-auto rounded-full bg-cream flex items-center justify-center text-ink/40 mb-3">
            <IconSearch width={22} height={22} />
          </div>
          <p className="text-sm text-ink/60">{error}</p>
        </div>
      )}

      {resultados?.map((d) => (
        <div key={d.id} className="bg-white rounded-2xl border border-mist/60 overflow-hidden">
          {/* Encabezado */}
          <div className="flex items-center gap-4 p-6 border-b border-mist/50">
            <div className="w-12 h-12 rounded-xl bg-steel/15 text-steel flex items-center justify-center">
              <IconBox width={24} height={24} />
            </div>
            <div className="flex-1">
              <p className="text-xs text-ink/50">Dosímetro · id interno {d.id}</p>
              <h2 className="text-2xl font-bold text-ink leading-tight">#{d.numero}</h2>
            </div>
            <div className="flex flex-col items-end gap-1">
              <Badge color={estadoColor[d.estado] || 'slate'}>{d.estado}</Badge>
              {d.observacion && <Badge color="amber">{d.observacion}</Badge>}
            </div>
          </div>

          {/* Info actual */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 p-6">
            <Chip etiqueta="Tipo" valor={d.tipoDosimetroNombre} />
            <Chip etiqueta="Porta actual" valor={d.tipoPortaNombre} />
            <Chip etiqueta="Tarea actual" valor={d.numeroTarea} />
            <Chip
              etiqueta="Bandeja / Slot"
              valor={d.numeroBandeja != null ? `${d.numeroBandeja} / ${d.slotBandeja}` : '—'}
            />
          </div>

          {/* Historial como línea de tiempo */}
          <div className="px-6 pb-6">
            <h3 className="text-sm font-semibold text-ink mb-4">
              Historial de asignaciones · {d.asignaciones.length}
            </h3>
            {d.asignaciones.length === 0 ? (
              <p className="text-sm text-slate-400">Sin asignaciones registradas</p>
            ) : (
              <ol className="relative border-l-2 border-mist/70 ml-2 space-y-5">
                {d.asignaciones.map((a) => (
                  <li key={a.id} className="ml-5">
                    <span className="absolute -left-[7px] w-3 h-3 rounded-full bg-steel border-2 border-white" />
                    <div className="flex flex-wrap items-center gap-2">
                      <Badge color="blue">{a.trimestre}</Badge>
                      <span className="text-xs text-ink/50">{a.fechaAsignacion}</span>
                    </div>
                    <p className="text-sm font-semibold text-ink mt-1">{a.clienteNombre}</p>
                    <p className="text-xs text-ink/60">
                      {a.ejecutivoNombre} · {a.empresaNombre}
                      {a.numeroTarea ? ` · Tarea ${a.numeroTarea}` : ''}
                      {a.numeroBandeja != null ? ` · Bandeja ${a.numeroBandeja}/${a.slotBandeja}` : ''}
                    </p>
                    {a.linkTrello && (
                      <a
                        href={a.linkTrello}
                        target="_blank"
                        rel="noreferrer"
                        className="text-xs text-steel hover:underline"
                      >
                        Ver Trello
                      </a>
                    )}
                  </li>
                ))}
              </ol>
            )}
          </div>
        </div>
      ))}
    </div>
  )
}
