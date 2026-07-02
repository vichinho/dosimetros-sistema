# Frontend — Sistema de Gestión de Dosímetros

SPA en React + Vite + Tailwind CSS que consume la API del backend Spring Boot.

## Requisitos
- Node 20+ (probado con Node 22)
- Backend corriendo en `http://localhost:8080`

## Desarrollo
```bash
npm install
npm run dev
```
La app queda en `http://localhost:5173`. En desarrollo, las peticiones a `/api`
se redirigen automáticamente al backend (`localhost:8080`) vía el proxy de Vite,
por lo que no hace falta configurar CORS.

## Build de producción
```bash
npm run build      # genera dist/
npm run preview    # sirve el build localmente
```
En producción se sirve `dist/` detrás de un servidor (ej. Nginx) que además
reenvíe `/api` al backend.

## Acceso inicial
Usuario semilla del backend: `admin` / `admin123`.

## Estructura
```
src/
├── api/          # cliente axios (JWT) y wrappers de endpoints
├── auth/         # contexto de sesión y rutas protegidas por rol
├── components/   # Layout y componentes de UI reutilizables
└── pages/        # pantallas (login, dashboard, stock, asignar, etc.)
```

## Roles y pantallas
- **ADMIN**: dashboard (KPIs), stock, asignar, buscar, clientes, ejecutivos,
  duplicados, importar Excel.
- **OPERADOR**: stock, asignar, buscar, clientes, ejecutivos, duplicados.
- **EJECUTIVO**: mis dosímetros (lotes enviados) y mis clientes.
