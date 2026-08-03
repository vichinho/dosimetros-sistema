import { useState } from 'react'
import { compararDosimetria, descargarPlantillaComparador } from '../api/endpoints'
import { Card, Button, Alert } from '../components/ui'
import { useToast } from '../components/Toast'

// Lee el mensaje de error cuando la respuesta viene como Blob (responseType blob).
async function mensajeError(err, fallback) {
  const data = err.response?.data
  if (data instanceof Blob) {
    try {
      const txt = await data.text()
      try { return JSON.parse(txt).message || fallback } catch { return txt || fallback }
    } catch { return fallback }
  }
  return err.response?.data?.message || fallback
}

function descargar(blob, nombre) {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = nombre
  a.click()
  URL.revokeObjectURL(url)
}

export default function Comparador() {
  const [softwareFile, setSoftwareFile] = useState(null)
  const [clientFile, setClientFile] = useState(null)
  const [comparando, setComparando] = useState(false)
  const [descargandoPlantilla, setDescargandoPlantilla] = useState(false)
  const [error, setError] = useState('')
  const toast = useToast()

  const comparar = async (e) => {
    e.preventDefault()
    setError('')
    if (!softwareFile) return setError('Selecciona el archivo del software.')
    if (!clientFile) return setError('Selecciona el archivo del cliente.')
    setComparando(true)
    try {
      const blob = await compararDosimetria(softwareFile, clientFile)
      descargar(blob, 'comparativo_dosimetria.xlsx')
      toast.success('Comparación lista: se descargó el Excel con los resultados.')
    } catch (err) {
      const msg = await mensajeError(err, 'No se pudo realizar la comparación')
      setError(msg)
      toast.error(msg)
    } finally {
      setComparando(false)
    }
  }

  const bajarPlantilla = async () => {
    setDescargandoPlantilla(true)
    try {
      const blob = await descargarPlantillaComparador()
      descargar(blob, 'plantilla_listado_cliente.xlsx')
    } catch {
      toast.error('No se pudo descargar la plantilla')
    } finally {
      setDescargandoPlantilla(false)
    }
  }

  const inputFile = 'block w-full text-sm text-slate-600 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:bg-steel file:text-white hover:file:bg-steel/90'

  return (
    <div className="space-y-6 max-w-2xl">
      <div>
        <h1 className="text-2xl font-bold text-ink">Comparador de listados</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Compara el listado del <b>software</b> con el listado del <b>cliente</b> y descarga un
          Excel con los resultados (mantener, quitar, nuevo, verificar). No guarda nada en el sistema.
        </p>
      </div>

      <Card title="Plantilla del cliente (opcional)">
        <p className="text-sm text-slate-500 mb-3">
          Si el cliente completa esta plantilla, la comparación es más precisa. Si no, el sistema
          intenta detectar las columnas automáticamente.
        </p>
        <Button type="button" variant="secondary" onClick={bajarPlantilla} disabled={descargandoPlantilla}>
          {descargandoPlantilla ? 'Descargando…' : 'Descargar plantilla del cliente'}
        </Button>
      </Card>

      <Card title="Comparar">
        <form onSubmit={comparar} className="space-y-5">
          <div>
            <label className="block text-sm font-medium text-ink/70 mb-1.5">
              1. Archivo del software (.xlsx)
            </label>
            <input type="file" accept=".xlsx" onChange={(e) => setSoftwareFile(e.target.files[0])} className={inputFile} />
          </div>
          <div>
            <label className="block text-sm font-medium text-ink/70 mb-1.5">
              2. Archivo del cliente (.xlsx)
            </label>
            <input type="file" accept=".xlsx" onChange={(e) => setClientFile(e.target.files[0])} className={inputFile} />
          </div>

          {error && <Alert type="error">{error}</Alert>}

          <Button type="submit" disabled={comparando}>
            {comparando ? 'Comparando…' : 'Comparar y descargar Excel'}
          </Button>
        </form>
      </Card>
    </div>
  )
}
