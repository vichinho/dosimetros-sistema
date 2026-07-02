# Sistema de Gestión de Dosímetros

Aplicación web para administrar dosímetros (dispositivos de medición de
radiación) que se asignan a clientes: control de stock, armado, asignación
masiva, historial y dashboard. Reemplaza el manejo previo en Excel
(~215K filas) por un sistema relacional con API y una SPA.

---

## Índice
- [Arquitectura y stack](#arquitectura-y-stack)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Requisitos](#requisitos)
- [Puesta en marcha (desarrollo)](#puesta-en-marcha-desarrollo)
- [Base de datos y migraciones](#base-de-datos-y-migraciones)
- [Migración del histórico](#migración-del-histórico)
- [Variables de entorno](#variables-de-entorno)
- [Roles y funcionalidades](#roles-y-funcionalidades)
- [Tests](#tests)
- [Seguridad](#seguridad)
- [Despliegue](#despliegue)
- [Power BI](#power-bi)

---

## Arquitectura y stack

| Capa | Tecnología |
|---|---|
| Frontend | React + Vite + Tailwind CSS + Recharts |
| Backend | Java 17+ / Spring Boot 3 |
| Seguridad | Spring Security + JWT |
| ORM | Spring Data JPA / Hibernate |
| Migraciones | Flyway |
| Carga Excel | Apache POI |
| Base de datos | MySQL 8 |
| Dashboards | Power BI (se conecta a MySQL) |

El frontend consume la API REST del backend. En desarrollo, Vite hace de
proxy de `/api` hacia el backend (mismo origen, sin CORS). En producción se
sirve detrás de Nginx.

## Estructura del repositorio

```
dosimetros-sistema/
├── backend/            # API Spring Boot (Maven)
│   ├── src/main/java/com/dosimetros/backend/
│   │   ├── config/ controller/ dto/ entity/ repository/
│   │   ├── service/ security/ exception/
│   │   └── resources/db/migration/   # migraciones Flyway (V1, V2, V3)
│   ├── src/test/       # pruebas (JUnit + Mockito + H2)
│   └── .env.example
├── frontend/           # SPA React + Vite
│   └── src/{api,auth,components,pages}
├── migracion/          # script Python para migrar el histórico del Excel
└── deploy/             # nginx.conf.example y SEGURIDAD.md
```

## Requisitos
- **Java 17+** y Maven (incluye `mvnw`).
- **Node 20+** (probado con Node 22).
- **MySQL 8** en ejecución.

## Puesta en marcha (desarrollo)

### 1. Base de datos
Solo necesitas un MySQL corriendo. La aplicación crea la base y el esquema
automáticamente (ver siguiente sección). Ajusta credenciales por variables
de entorno si tu MySQL no usa `root` / `1234`.

### 2. Backend
```bash
cd backend
./mvnw spring-boot:run
```
Queda en `http://localhost:8080`. Al primer arranque, Flyway crea el esquema
y carga los catálogos (empresas, tipos, portas, roles) y un usuario admin.

### 3. Frontend
```bash
cd frontend
npm install
npm run dev
```
Queda en `http://localhost:5173`.

### 4. Acceso inicial
Usuario semilla: **`admin` / `admin123`** (cámbialo tras el primer ingreso).

## Base de datos y migraciones
El esquema lo gestiona **Flyway** (`backend/src/main/resources/db/migration`):
- `V1` — esquema inicial (tablas, FKs, índices).
- `V2` — datos semilla (roles, empresas, tipos de dosímetro y porta, admin).
- `V3` — ejecutivo responsable en cliente.

`ddl-auto=validate`: Hibernate valida el esquema contra las entidades; Flyway
corre antes. La URL JDBC usa `createDatabaseIfNotExist=true`.

> Si necesitas recargar desde cero en dev: `DROP DATABASE dosimetros_db;` y
> vuelve a arrancar el backend.

## Migración del histórico
Para cargar el histórico real desde el Excel `REPOSITORIO`, usa el script en
`migracion/` (genera un `.sql` que corres en MySQL). Ver
[`migracion/README.md`](migracion/README.md).

## Variables de entorno
En desarrollo funcionan los valores por defecto. En producción **define**:

| Variable | Descripción |
|---|---|
| `SPRING_PROFILES_ACTIVE=prod` | Activa validaciones de seguridad de arranque |
| `JWT_SECRET` | Secreto JWT (≥ 32 caracteres aleatorios) |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Conexión a MySQL |
| `CORS_ALLOWED_ORIGINS` | Orígenes del frontend (coma-separados) |

Plantilla: [`backend/.env.example`](backend/.env.example).

## Roles y funcionalidades

| Rol | Puede |
|---|---|
| **ADMIN** | Todo: dashboard (KPIs), stock, armar, asignar, buscar, clientes, ejecutivos, duplicados, tipos de porta, importar Excel, **usuarios** |
| **OPERADOR** | Stock, armar, asignar, buscar, clientes, ejecutivos, duplicados, tipos de porta |
| **EJECUTIVO** | Solo consulta de lo suyo: mis dosímetros (lotes), mis asignaciones (con filtros), mis clientes |

Funciones clave: carga masiva por Excel, armado por rango de bandeja/slot,
asignación masiva con validación de compatibilidad y stock, liberar/reutilizar,
marcar dañado/bueno, dar de baja, duplicados/stock de emergencia, búsqueda con
historial, dashboard estilo Power BI con drill-down, exportar stock a Excel.

## Tests
```bash
cd backend
./mvnw test
```
Pruebas unitarias (Mockito) de la lógica crítica —asignación masiva,
compatibilidad, estados, rate limiting— y carga de contexto sobre H2 en
memoria (no requiere MySQL).

## Seguridad
Controles incluidos: BCrypt, JWT firmado, autorización por rol en todos los
endpoints, scoping por ejecutivo, rate limiting del login, cabeceras de
seguridad, límites/anti zip-bomb en la carga de Excel, secretos por entorno y
bloqueo de arranque en `prod` con secretos por defecto.

Checklist completo de despliegue seguro: [`deploy/SEGURIDAD.md`](deploy/SEGURIDAD.md).

Análisis de vulnerabilidades de dependencias:
```bash
cd backend && ./mvnw dependency-check:check
```

## Despliegue
- Servir el build del frontend (`frontend/dist`) y reenviar `/api` al backend
  con Nginx: ver [`deploy/nginx.conf.example`](deploy/nginx.conf.example).
- Terminar TLS (HTTPS) en Nginx.
- Definir las variables de entorno de producción y `SPRING_PROFILES_ACTIVE=prod`.

## Power BI
Los KPIs están disponibles vía API (`GET /api/dashboard/kpis`) y, para
reportes, Power BI puede conectarse directamente a MySQL con el conector
nativo.
```
