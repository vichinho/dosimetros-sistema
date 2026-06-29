import client from './client'

// --- Autenticación ---
export const login = (username, password) =>
  client.post('/auth/login', { username, password }).then((r) => r.data)

// --- Catálogos ---
export const getEmpresas = () => client.get('/empresas').then((r) => r.data)
export const getTiposDosimetro = () => client.get('/tipos-dosimetro').then((r) => r.data)
export const getTiposPorta = () => client.get('/tipos-porta').then((r) => r.data)
export const getTareas = () => client.get('/tareas').then((r) => r.data)

// --- Dashboard ---
export const getKpis = () => client.get('/dashboard/kpis').then((r) => r.data)

// --- Dosímetros ---
export const getStock = (params) =>
  client.get('/dosimetros/stock', { params }).then((r) => r.data)
export const getDisponibles = () => client.get('/dosimetros/disponibles').then((r) => r.data)
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

// --- Clientes ---
export const getClientes = () => client.get('/clientes').then((r) => r.data)
export const crearCliente = (data) => client.post('/clientes', data).then((r) => r.data)
export const actualizarCliente = (id, data) =>
  client.put(`/clientes/${id}`, data).then((r) => r.data)
export const desactivarCliente = (id) => client.patch(`/clientes/${id}/desactivar`)

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
export const getMisAsignaciones = () =>
  client.get('/ejecutivo/mis-asignaciones').then((r) => r.data)
export const getMisLotes = () => client.get('/ejecutivo/mis-lotes').then((r) => r.data)
