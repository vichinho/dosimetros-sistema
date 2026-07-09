# Manual de usuario — Sistema de Gestión de Dosímetros

Este documento describe cada pantalla (módulo) del sistema, organizada por rol.
Para cada módulo se indica: para qué sirve, qué muestra, sus funcionalidades y
notas de uso.

## Roles y acceso

El sistema tiene tres roles. El **Administrador** ve todo lo del **Operador** y,
además, tres módulos propios. El **Ejecutivo** tiene una vista acotada a su
propia cartera.

| Rol | Módulos a los que accede |
|---|---|
| **Administrador** | Dashboard · Importar Excel · Usuarios · **+ todos los de Operador** |
| **Operador** | Stock · Armar · Asignar · Dosímetros · Pendiente de envío · Clientes · Ejecutivos · Duplicados · Tipos de porta |
| **Ejecutivo** | Pendiente de envío (solo lo suyo) · Mis dosímetros · Mis asignaciones · Mis clientes |

> Convención: para no repetir, los módulos compartidos entre Administrador y
> Operador se describen una sola vez, en la sección **Operador**. En la sección
> **Administrador** solo se detallan los tres módulos exclusivos.

---

# 1. Módulos de Administrador

El administrador tiene acceso total. Además de todos los módulos operativos
(ver sección Operador), cuenta con tres pantallas propias: **Dashboard**,
**Importar Excel** y **Usuarios**.

## 1.1 Dashboard

**Acceso:** solo Administrador. Es la pantalla de inicio al ingresar como
administrador.

**Propósito.** Ofrecer una vista ejecutiva para la toma de decisiones: de un
vistazo, cuánto inventario hay y en qué estado, y cómo se comporta la actividad
de asignaciones. Todo es interactivo (se puede tocar cada indicador para ver el
detalle) y se puede acotar por trimestre.

### Qué muestra

El tablero tiene tres zonas:

**1) Indicadores principales (tarjetas clicables).** Cambian según haya o no un
trimestre seleccionado:

- **Sin trimestre (visión de inventario actual):**
  - *Total dosímetros* — cuántos hay en total.
  - *Disponibles* — listos para asignar (con su % sobre el total).
  - *Asignados* — actualmente en uso (con su %).
  - *Dañados* — marcados como dañados (no asignables).
  - *Asignaciones* — total histórico de asignaciones.
- **Con un trimestre seleccionado (visión de ese trimestre):**
  - *Asignaciones · [trimestre]* — cuántas se hicieron en el trimestre.
  - *Clientes atendidos* — clientes distintos con asignaciones en el trimestre.
  - *Empresas* — empresas distintas involucradas.
  - *Portas usadas* — tipos de porta distintos utilizados.
  - *Ejecutivos* — ejecutivos distintos con actividad.

Cada tarjeta es un botón: al tocarla se abre una ventana de **detalle**
(por ejemplo, "Asignados" abre el desglose por empresa y por ejecutivo).

**2) Gráficos.** También reaccionan al filtro de trimestre:

- Sin trimestre: dona de *Dosímetros por estado*, línea de *Asignaciones por
  trimestre*, y barras de *Disponibles por porta*, *Dosímetros por tipo*,
  *Asignaciones por empresa*, *por tipo de porta*, *por ejecutivo* y
  *Top 3 clientes*.
- Con un trimestre: se ocultan los gráficos de inventario (que no dependen del
  trimestre) y el gráfico de evolución pasa de "por trimestre" a **"Asignaciones
  por mes"**, mostrando los tres meses de ese trimestre. Los gráficos de
  asignaciones quedan acotados al trimestre elegido.

**3) Stock histórico por fecha.** Un selector de fecha que responde a la
pregunta *"¿cuántos dosímetros existían a tal fecha?"*. El cálculo se basa en la
**fecha de creación** de cada dosímetro (cuándo entró al inventario), no en las
asignaciones. Muestra el total y el desglose por tipo de porta y por tipo de
dosímetro a esa fecha.

### Funcionalidades

- **Filtro de trimestre reactivo:** al elegir un trimestre, todo el tablero
  (tarjetas y gráficos) se actualiza a ese período. El selector indica también
  los meses que abarca (por ejemplo, `2T2025 (abr–jun)`), y aparece un
  indicador "actualizando…" mientras recalcula.
- **Indicadores clicables (drill-down):** cada tarjeta abre el detalle en una
  ventana emergente, sin salir del tablero.
- **Consulta de stock histórico** a cualquier fecha pasada.

### Qué cumple / para qué se usa

- Responder rápidamente "¿cuánto tengo, disponible, asignado y dañado?".
- Medir la actividad de un trimestre (asignaciones, clientes, empresas,
  ejecutivos, portas).
- Comparar la evolución mensual dentro de un trimestre.
- Saber el tamaño del inventario en una fecha del pasado.

### Notas

- Los indicadores de inventario (total, disponibles, dañados) son un dato de
  "ahora": no cambian por trimestre porque reflejan el estado físico actual del
  stock. Por eso, al filtrar por trimestre, el bloque superior pasa a mostrar
  métricas propias del trimestre.

## 1.2 Importar Excel

_(pendiente de completar)_

## 1.3 Usuarios

_(pendiente de completar)_

---

# 2. Módulos de Operador

_(pendiente de completar: Stock, Armar, Asignar, Dosímetros, Pendiente de envío,
Clientes, Ejecutivos, Duplicados, Tipos de porta)_

---

# 3. Módulos de Ejecutivo

_(pendiente de completar: Pendiente de envío, Mis dosímetros, Mis asignaciones,
Mis clientes)_
