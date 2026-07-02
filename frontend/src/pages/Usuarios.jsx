import { useEffect, useState } from 'react'
import {
  getUsuarios,
  crearUsuario,
  actualizarUsuario,
  desactivarUsuario,
  getRoles,
  getEjecutivos,
} from '../api/endpoints'
import { Card, Button, Input, Select, Badge, Loading, EmptyState } from '../components/ui'
import { useToast } from '../components/Toast'

const VACIO = { username: '', password: '', rolId: '', ejecutivoId: '' }

export default function Usuarios() {
  const [usuarios, setUsuarios] = useState([])
  const [roles, setRoles] = useState([])
  const [ejecutivos, setEjecutivos] = useState([])
  const [form, setForm] = useState(VACIO)
  const [editId, setEditId] = useState(null)
  const [loading, setLoading] = useState(true)
  const toast = useToast()

  const cargar = () =>
    getUsuarios()
      .then(setUsuarios)
      .catch(() => toast.error('No se pudieron cargar los usuarios'))
      .finally(() => setLoading(false))

  useEffect(() => {
    cargar()
    getRoles().then(setRoles).catch(() => {})
    getEjecutivos().then(setEjecutivos).catch(() => {})
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const rolSeleccionado = roles.find((r) => String(r.id) === String(form.rolId))
  const esEjecutivo = rolSeleccionado?.nombre === 'EJECUTIVO'

  const resetForm = () => {
    setForm(VACIO)
    setEditId(null)
  }

  const editar = (u) => {
    const rol = roles.find((r) => r.nombre === u.rol)
    setEditId(u.id)
    setForm({
      username: u.username,
      password: '',
      rolId: rol ? String(rol.id) : '',
      ejecutivoId: u.ejecutivoId ? String(u.ejecutivoId) : '',
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (esEjecutivo && !form.ejecutivoId) {
      toast.error('Selecciona el ejecutivo asociado')
      return
    }
    try {
      if (editId) {
        await actualizarUsuario(editId, {
          rolId: Number(form.rolId),
          ejecutivoId: esEjecutivo && form.ejecutivoId ? Number(form.ejecutivoId) : null,
          password: form.password ? form.password : null,
        })
        toast.success('Usuario actualizado')
      } else {
        await crearUsuario({
          username: form.username.trim(),
          password: form.password,
          rolId: Number(form.rolId),
          ejecutivoId: esEjecutivo && form.ejecutivoId ? Number(form.ejecutivoId) : null,
        })
        toast.success('Usuario creado correctamente')
      }
      resetForm()
      cargar()
    } catch (err) {
      toast.error(err.response?.data?.message || 'No se pudo guardar el usuario')
    }
  }

  const handleDesactivar = async (id) => {
    try {
      await desactivarUsuario(id)
      toast.success('Usuario desactivado')
      if (editId === id) resetForm()
      cargar()
    } catch {
      toast.error('No se pudo desactivar')
    }
  }

  const colorRol = { ADMIN: 'blue', OPERADOR: 'green', EJECUTIVO: 'slate' }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-ink">Usuarios</h1>

      <Card title={editId ? `Editar usuario: ${form.username}` : 'Nuevo usuario'}>
        <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4 items-end">
          <Input
            label="Usuario"
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
            required
            disabled={!!editId}
          />
          <Input
            label={editId ? 'Nueva contraseña (opcional)' : 'Contraseña'}
            type="password"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required={!editId}
            placeholder={editId ? 'Dejar vacío para mantener' : ''}
          />
          <Select
            label="Rol"
            value={form.rolId}
            onChange={(e) => setForm({ ...form, rolId: e.target.value, ejecutivoId: '' })}
            required
          >
            <option value="">Selecciona…</option>
            {roles.map((r) => (
              <option key={r.id} value={r.id}>{r.nombre}</option>
            ))}
          </Select>
          <Select
            label="Ejecutivo asociado"
            value={form.ejecutivoId}
            onChange={(e) => setForm({ ...form, ejecutivoId: e.target.value })}
            disabled={!esEjecutivo}
          >
            <option value="">{esEjecutivo ? 'Selecciona…' : 'Solo para rol EJECUTIVO'}</option>
            {ejecutivos.map((ej) => (
              <option key={ej.id} value={ej.id}>{ej.nombre}</option>
            ))}
          </Select>
          <div className="flex gap-2">
            <Button type="submit">{editId ? 'Guardar' : 'Crear usuario'}</Button>
            {editId && (
              <Button type="button" variant="secondary" onClick={resetForm}>
                Cancelar
              </Button>
            )}
          </div>
        </form>
      </Card>

      <Card title={`Usuarios activos (${usuarios.length})`}>
        {loading ? (
          <Loading />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-slate-500 border-b border-slate-200">
                  <th className="py-2 font-medium">Usuario</th>
                  <th className="py-2 font-medium">Rol</th>
                  <th className="py-2 font-medium">Ejecutivo</th>
                  <th className="py-2 font-medium">Estado</th>
                  <th className="py-2"></th>
                </tr>
              </thead>
              <tbody>
                {usuarios.map((u) => (
                  <tr key={u.id} className="border-b border-slate-100">
                    <td className="py-2.5 font-medium text-ink">{u.username}</td>
                    <td className="py-2.5">
                      <Badge color={colorRol[u.rol] || 'slate'}>{u.rol}</Badge>
                    </td>
                    <td className="py-2.5 text-slate-600">{u.ejecutivoNombre || '—'}</td>
                    <td className="py-2.5">
                      <Badge color={u.activo ? 'green' : 'red'}>{u.activo ? 'Activo' : 'Inactivo'}</Badge>
                    </td>
                    <td className="py-2.5 text-right space-x-3">
                      <button onClick={() => editar(u)} className="text-steel hover:underline text-sm">
                        Editar
                      </button>
                      {u.activo && (
                        <button
                          onClick={() => handleDesactivar(u.id)}
                          className="text-red-600 hover:underline text-sm"
                        >
                          Desactivar
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
                {usuarios.length === 0 && (
                  <tr>
                    <td colSpan="5">
                      <EmptyState>No hay usuarios</EmptyState>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  )
}
