# --- Etapa 1: compilar el frontend (React + Vite) ---
FROM node:20-alpine AS build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# --- Etapa 2: Caddy sirve el SPA y gestiona el HTTPS automático ---
FROM caddy:2-alpine
# Archivos estáticos del frontend ya compilados.
COPY --from=build /app/dist /srv
# Configuración: sirve /srv y reenvía /api al backend.
COPY deploy/Caddyfile /etc/caddy/Caddyfile
