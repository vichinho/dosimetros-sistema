# Sistema de Gestión de Dosímetros

## ¿De qué trata el proyecto?

Los **dosímetros** son dispositivos que miden la radiación a la que se expone
una persona. La empresa administra un gran inventario de dosímetros que se
**asignan a clientes** de sectores como salud, minería y construcción, se
**reutilizan** entre trimestres y se gestionan bajo dos marcas comerciales.

Hasta ahora todo se llevaba en una planilla Excel de cientos de miles de
filas: lenta, difícil de consultar y propensa a errores. Este proyecto la
reemplaza por una **aplicación web** que centraliza y ordena todo el proceso:

- **Inventario y armado** de dosímetros (por tipo: OSL, TLD, Cristal, y su
  tipo de porta), organizados en tareas/bandejas/slots.
- **Asignación masiva** a clientes (cientos o miles a la vez) con validación
  de compatibilidad y de stock disponible.
- **Reutilización y trazabilidad**: cada dosímetro conserva su historial
  completo de asignaciones y ubicaciones.
- **Consulta por rol**: administradores y operadores gestionan todo; cada
  ejecutivo ve solo la información de sus clientes.
- **Dashboard** con indicadores y gráficos para la toma de decisiones.

## Características principales

- Carga masiva de dosímetros desde Excel.
- Armado por rango de bandeja/slot dentro de una tarea.
- Asignación y reasignación masiva; liberar y dar de baja.
- Marcado de dosímetros dañados y de duplicados / stock de emergencia.
- Búsqueda por número con historial completo.
- Gestión de clientes, ejecutivos, tipos de porta y usuarios.
- Dashboard con KPIs, filtros por trimestre y detalle interactivo.
- Exportación de stock a Excel.

### Roles
| Rol | Alcance |
|---|---|
| **Administrador** | Acceso total: catálogos, usuarios, stock, asignaciones y dashboard. |
| **Operador** | Armar y asignar dosímetros, gestionar clientes/ejecutivos/portas, ver todo. |
| **Ejecutivo** | Solo consulta de sus clientes y dosímetros asignados. |

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Frontend | React + Vite + Tailwind CSS + Recharts |
| Backend | Java 17+ / Spring Boot 3 |
| Seguridad | Spring Security + JWT |
| Datos | Spring Data JPA / Hibernate · MySQL 8 · Flyway |
| Carga Excel | Apache POI |
| Dashboards | Power BI (conector nativo a MySQL) |

## Estructura del repositorio

```
dosimetros-sistema/
├── backend/     # API REST (Spring Boot) + migraciones Flyway + tests
├── frontend/    # SPA (React + Vite)
├── migracion/   # utilidades para migrar el histórico desde Excel
└── deploy/      # ejemplo de Nginx y guía de seguridad
```

## Requisitos
- Java 17+ (incluye `mvnw`)
- Node 20+
- MySQL 8

## Puesta en marcha (desarrollo)

Con MySQL en ejecución:

```bash
# Backend  → http://localhost:8080
cd backend && ./mvnw spring-boot:run

# Frontend → http://localhost:5173
cd frontend && npm install && npm run dev
```

Al primer arranque, Flyway crea el esquema y carga los catálogos base y un
usuario administrador inicial. **Las credenciales de acceso se entregan de
forma interna y deben cambiarse tras el primer ingreso** (ver
[`deploy/SEGURIDAD.md`](deploy/SEGURIDAD.md)).

La configuración (conexión a BD, secretos, orígenes CORS) se toma de
**variables de entorno**; para desarrollo hay valores por defecto. Plantilla
de variables: [`backend/.env.example`](backend/.env.example).

## Base de datos y migraciones
El esquema y los datos base los gestiona **Flyway**
(`backend/src/main/resources/db/migration`). Para migrar el histórico real
desde el Excel, ver [`migracion/README.md`](migracion/README.md).

## Tests
```bash
cd backend && ./mvnw test
```
Pruebas unitarias de la lógica crítica y carga de contexto sobre H2 (no
requieren MySQL).

## Seguridad y despliegue
- Checklist de despliegue seguro y configuración de producción:
  [`deploy/SEGURIDAD.md`](deploy/SEGURIDAD.md).
- Ejemplo de Nginx (sirve el frontend y reenvía `/api`):
  [`deploy/nginx.conf.example`](deploy/nginx.conf.example).

## Power BI
Los indicadores están disponibles vía API (`/api/dashboard/kpis`) y, para
reportes, Power BI puede conectarse directamente a MySQL.
