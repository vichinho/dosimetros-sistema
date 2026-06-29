import { useState } from 'react'
import client from '../api/client'
import { Card, Button, Alert } from '../components/ui'

export default function Importar() {
  const [file, setFile] = useState(null)
  const [resultado, setResultado] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setResultado(null)
    if (!file) {
      setError('Selecciona un archivo .xlsx')
      return
    }
    const formData = new FormData()
    formData.append('file', file)
    setLoading(true)
    try {
      const { data } = await client.post('/dosimetros/importacion/excel', formData)
      setResultado(data)
    } catch (err) {
      setError(err.response?.data?.message || 'No se pudo importar el archivo')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-6 max-w-2xl">
      <h1 className="text-2xl font-bold text-slate-800">Importar dosímetros (Excel)</h1>

      <Card>
        <form onSubmit={handleSubmit} className="space-y-4">
          <p className="text-sm text-slate-500">
            Sube un archivo <b>.xlsx</b> con la hoja de carga masiva (columnas: numero_dosimetro,
            tipo_dosimetro, tipo_porta, numero_tarea, numero_bandeja, slot_bandeja).
          </p>
          <input
            type="file"
            accept=".xlsx"
            onChange={(e) => setFile(e.target.files[0])}
            className="block w-full text-sm text-slate-600 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:bg-blue-600 file:text-white hover:file:bg-blue-700"
          />
          <Button type="submit" disabled={loading}>
            {loading ? 'Importando…' : 'Importar'}
          </Button>
        </form>
      </Card>

      <Alert type="error">{error}</Alert>

      {resultado && (
        <Card title="Resultado de la importación">
          <div className="flex gap-6 text-sm mb-4">
            <span>Total filas: <b>{resultado.totalFilas}</b></span>
            <span className="text-green-600">Exitosas: <b>{resultado.exitosas}</b></span>
            <span className="text-red-600">Fallidas: <b>{resultado.fallidas}</b></span>
          </div>
          {resultado.errores?.length > 0 && (
            <div className="bg-red-50 border border-red-200 rounded-lg p-3 max-h-60 overflow-auto">
              <ul className="text-sm text-red-700 space-y-1">
                {resultado.errores.map((e, i) => (
                  <li key={i}>{e}</li>
                ))}
              </ul>
            </div>
          )}
        </Card>
      )}
    </div>
  )
}
