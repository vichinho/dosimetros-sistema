import client from './client'

// --- Autenticación ---
export const login = (username, password) =>
  client.post('/auth/login', { username, password }).then((r) => r.data)

// --- Usuarios ---
export const getRoles = () => client.get('/roles').then((r) => r.data)
export const getUsuarios = () => client.get('/usuarios').then((r) => r.data)
export const crearUsuario = (data) => client.post('/usuarios', data).then((r) => r.data)
export const actualizarUsuario = (id, data) =>
  client.put(`/usuarios/${id}`, data).then((r) => r.data)
export const desactivarUsuario = (id) => client.patch(`/usuarios/${id}/desactivar`)

// --- Catálogos ---
export const getEmpresas = () => client.get('/empresas').then((r) => r.data)
export const getTiposDosimetro = () => client.get('/tipos-dosimetro').then((r) => r.data)
export const getTiposPorta = () => client.get('/tipos-porta').then((r) => r.data)
export const crearTipoPorta = (data) => client.post('/tipos-porta', data).then((r) => r.data)
export const actualizarTipoPorta = (id, data) =>
  client.put(`/tipos-porta/${id}`, data).then((r) => r.data)
export const eliminarTipoPorta = (id) => client.delete(`/tipos-porta/${id}`)
export const getTareas = () => client.get('/tareas').then((r) => r.data)
export const getTareasDisponibles = (tipoDosimetroId) =>
  client
    .get('/dosimetros/tareas-disponibles', {
      params: tipoDosimetroId ? { tipoDosimetroId } : {},
    })
    .then((r) => r.data)

// --- Dashboard ---
export const getKpis = (trimestre) =>
  client.get('/dashboard/kpis', { params: trimestre ? { trimestre } : {} }).then((r) => r.data)

export const getStockHistorico = (fecha) =>
  client.get('/dashboard/stock-historico', { params: fecha ? { fecha } : {} }).then((r) => r.data)

// --- Dosímetros ---
export const getStock = (params) =>
  client.get('/dosimetros/stock', { params }).then((r) => r.data)
export const getDisponibles = () => client.get('/dosimetros/disponibles').then((r) => r.data)
export const getPortasDisponibles = () =>
  client.get('/dosimetros/disponibles/portas').then((r) => r.data)
export const exportarStockExcel = (params) =>
  client.get('/dosimetros/stock/export', { params, responseType: 'blob' }).then((r) => r.data)
export const buscarDosimetro = (numero) =>
  client.get('/dosimetros/buscar', { params: { numero } }).then((r) => r.data)
export const getDuplicados = () => client.get('/dosimetros/duplicados').then((r) => r.data)
export const getHistorial = (id) =>
  client.get(`/dosimetros/${id}/historial`).then((r) => r.data)
export const marcarStockEmergencia = (id, observacion) =>
  client
    .patch(`/dosimetros/${id}/stock-emergencia`, null, { params: { observacion } })
    .then((r) => r.data)
export const darDeBaja = (id, observacion) =>
  client.patch(`/dosimetros/${id}/baja`, null, { params: { observacion } }).then((r) => r.data)
export const liberarDosimetro = (id) =>
  client.patch(`/dosimetros/${id}/liberar`).then((r) => r.data)
export const marcarDanado = (id) =>
  client.patch(`/dosimetros/${id}/danado`).then((r) => r.data)
export const marcarBueno = (id) =>
  client.patch(`/dosimetros/${id}/bueno`).then((r) => r.data)
export const actualizarTipoPortaRango = (data) =>
  client.patch('/dosimetros/rango-porta', data).then((r) => r.data)

// --- Clientes ---
export const getClientes = () => client.get('/clientes').then((r) => r.data)
export const crearCliente = (data) => client.post('/clientes', data).then((r) => r.data)
export const actualizarCliente = (id, data) =>
  client.put(`/clientes/${id}`, data).then((r) => r.data)
export const desactivarCliente = (id) => client.patch(`/clientes/${id}/desactivar`)
export const getAsignacionesPorCliente = (clienteId) =>
  client.get(`/asignaciones/cliente/${clienteId}`).then((r) => r.data)

// --- Ejecutivos ---
export const getEjecutivos = () => client.get('/ejecutivos').then((r) => r.data)
export const crearEjecutivo = (data) => client.post('/ejecutivos', data).then((r) => r.data)
export const actualizarEjecutivo = (id, data) =>
  client.put(`/ejecutivos/${id}`, data).then((r) => r.data)
export const desactivarEjecutivo = (id) => client.patch(`/ejecutivos/${id}/desactivar`)

// --- Asignaciones ---
export const asignarMasivo = (data) =>
  client.post('/asignaciones/masivo', data).then((r) => r.data)

// --- Vistas del ejecutivo ---
export const getMisClientes = () => client.get('/ejecutivo/mis-clientes').then((r) => r.data)
export const getMisAsignaciones = (params) =>
  client.get('/ejecutivo/mis-asignaciones', { params }).then((r) => r.data)
export const getMisFiltros = () => client.get('/ejecutivo/mis-filtros').then((r) => r.data)
export const getMisLotes = () => client.get('/ejecutivo/mis-lotes').then((r) => r.data)
