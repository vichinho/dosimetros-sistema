import axios from 'axios'

// baseURL '/api' funciona con el proxy de Vite en dev y detrás de Nginx en prod.
const client = axios.create({
  baseURL: '/api',
})

// Adjunta el token JWT a cada petición si existe.
client.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Si el token expira o es inválido, limpia la sesión y vuelve al login.
client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('username')
      sessionStorage.removeItem('rol')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default client
