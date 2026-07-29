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

**Acceso:** solo Administrador.

**Propósito.** Cargar el inventario de dosímetros de forma masiva desde una
planilla Excel, en lugar de ingresarlos uno por uno. Es la vía de **alta
inicial** de dosímetros al sistema.

### Qué se ve en pantalla

- Una tarjeta con las **instrucciones** y las columnas esperadas.
- Un botón **"Descargar plantilla de ejemplo"**.
- Un **selector de archivo** (.xlsx) y el botón **"Importar"**.
- Tras importar, una tarjeta de **resultado** con los totales (filas
  procesadas, exitosas, fallidas) y el **detalle de errores** por fila.

### Funcionalidades

- **Descargar plantilla:** genera un archivo `.xlsx` con los encabezados
  correctos y una fila de ejemplo ya rellenada, para guiar el llenado.
- **Importar archivo:** sube la planilla y crea los dosímetros.
- **Resumen y control de errores:** informa cuántas filas se cargaron, cuántas
  se omitieron o fallaron y por qué (fila por fila).

### Columnas de la planilla

| Columna | Obligatoria | Descripción |
|---|---|---|
| `numero_dosimetro` | Sí | Número físico del dosímetro. |
| `tipo_dosimetro` | Sí | TLD, OSL o Cristal. |
| `tipo_porta` | No | Tipo de porta (debe ser compatible con el tipo). |
| `numero_tarea` | No | Tarea/lote (vacío para OSL). |
| `numero_bandeja` | No | Número de bandeja (vacío para OSL). |
| `slot_bandeja` | No | Posición dentro de la bandeja (vacío para OSL). |

### Qué cumple / notas

- Reemplaza la carga manual y la antigua planilla histórica.
- **No duplica:** si un número ya existe en el sistema, o se repite dentro del
  mismo archivo, esa fila se **omite** y se informa (no crea un duplicado).
- Esta pantalla es de **alta** (crear). Para **actualizar** stock ya existente
  desde un archivo se usa "Actualizar stock por archivo" dentro de **Stock**.

## 1.3 Usuarios

**Acceso:** solo Administrador.

**Propósito.** Administrar quién puede entrar al sistema y con qué nivel de
acceso: crear, editar y desactivar usuarios, y asignarles su rol.

### Qué se ve en pantalla

- Un **formulario** de alta/edición con: Usuario, Contraseña, Rol y
  "Ejecutivo asociado".
- Una **tabla de usuarios** con: nombre de usuario, rol (con color), ejecutivo
  vinculado, estado (Activo/Inactivo) y acciones (**Editar**, **Desactivar**).

### Funcionalidades

- **Crear usuario:** nombre de usuario, contraseña, rol y —solo si el rol es
  Ejecutivo— el ejecutivo al que queda asociado.
- **Editar usuario:** cambiar rol y ejecutivo asociado, y **restablecer la
  contraseña** (si se deja vacía, se mantiene la actual). El nombre de usuario
  no se modifica.
- **Desactivar usuario:** deja al usuario sin acceso conservando su registro
  (baja lógica, no se borra).

### Roles disponibles

- **Administrador:** acceso total, incluidos Dashboard, Importar y Usuarios.
- **Operador:** gestión operativa (stock, armado, asignación, catálogos), sin
  los módulos exclusivos del administrador.
- **Ejecutivo:** solo consulta de su propia cartera. **Requiere** vincularse a
  un ejecutivo: ese vínculo es lo que hace que en sus pantallas "Mis…" vea
  únicamente sus clientes, dosímetros y asignaciones.

### Notas

- Las contraseñas se guardan cifradas (no se almacenan en texto plano).
- Un usuario con rol Ejecutivo **sin** ejecutivo asociado no podría ver su
  información; por eso el sistema exige elegirlo al crearlo.

---

# 2. Módulos de Operador

El operador realiza el trabajo diario: cargar y ordenar el stock, armar,
asignar, corregir y despachar. Estos módulos también están disponibles para el
Administrador.

## 2.1 Stock

**Acceso:** Administrador y Operador.

**Propósito.** Consultar y administrar el inventario de dosímetros: cuántos hay
por tipo de porta, navegar por tarea, ver el detalle de cada dosímetro y
mantener el stock actualizado.

### Qué se ve en pantalla

- **Stock por porta:** tarjetas con la cantidad disponible de **cada** tipo de
  porta, incluidas las que están en **0** (se muestran atenuadas).
- **Actualizar stock por archivo:** carga de un Excel para poner al día el
  inventario.
- **Selector de vista** (Tabla dinámica / Lista detallada) y **filtros**
  (tipo de dosímetro, estado de armado, estado).
- **Vista dinámica (matriz tarea × porta):** una tabla cruzada con la cantidad
  por tarea y por porta, con totales por fila, columna y general.
- **Lista detallada:** el listado de dosímetros uno por uno.

### Funcionalidades

- **Ver todas las portas**, incluso las que están en 0.
- **Vista dinámica** que se actualiza en vivo al cambiar los filtros; cada
  **tarea** y cada **celda** es clicable y abre el detalle de sus dosímetros.
- **Lista detallada** con acciones por dosímetro: liberar (vuelve a
  disponible), marcar dañado, marcar bueno; con paginación y **exportación a
  Excel**.
- **Actualizar stock por archivo (upsert por número):** si el número no existe
  se crea, si cambió se actualiza y si es idéntico se deja igual. Los números
  duplicados en el sistema o dosímetros ya asignados/de baja no se tocan y se
  reportan.

### Qué cumple

Es la pantalla central de inventario: responde "¿qué tengo y dónde?", permite
navegar por tarea, corregir estados puntuales y mantener el stock al día por
archivo.

## 2.2 Armar

**Acceso:** Administrador y Operador.

**Propósito.** "Armar" es asignar un **tipo de porta** a los dosímetros de una
tarea. Esta pantalla permite hacerlo de forma rápida y controlada.

### Qué se ve en pantalla

- **Estado de armado por tarea:** una tabla con **todas** las tareas, su barra
  de progreso (armados/total), el conteo de pendientes y un estado
  (**Armada**, **Parcial** o **Sin armar**). Incluye filtros (Todas / Con
  pendientes / Armadas) y buscador por tarea. Cada fila es clicable.
- Al elegir una tarea se abre su **mapa de bandejas**, con dos formas de armar:
  - **Modo rápido:** un rango de bandejas + tipo de porta arma muchas de una
    vez.
  - **Modo preciso (grilla tipo "sala de cine"):** para bandejas incompletas;
    los slots **pendientes** se ven verdes y seleccionables, y los ya
    **armados** quedan bloqueados en gris con su porta.

### Funcionalidades

- Resumen del avance de armado de todas las tareas, con filtros y búsqueda.
- Armado rápido por rango de bandejas (una acción por cada porta).
- Armado preciso slot por slot cuando una bandeja quedó incompleta.
- La información se **refresca sola** al armar.

### Notas

- Un dosímetro está **pendiente** cuando no tiene porta o cuando su porta es una
  "Sin armar (…)"; está **armado** cuando tiene una porta real.

## 2.3 Asignar

**Acceso:** Administrador y Operador.

**Propósito.** Asignar dosímetros a un cliente para un trimestre. Es el flujo
central de salida del inventario.

### Qué se ve en pantalla

- **Datos de la asignación** (comunes a todas las modalidades): cliente,
  ejecutivo, empresa, tipo de porta, trimestre y **link de Trello (obligatorio)**.
- Tres modalidades en **pestañas**:
  - **Masiva (por tareas):** se marcan tareas con stock disponible (mostrando el
    **total** disponible) y una cantidad; el sistema elige los dosímetros.
  - **Individual (por número):** se busca un dosímetro por su número y se asigna
    uno puntual.
  - **Por archivo:** carga de un Excel para asignar en lote.

### Funcionalidades

- **Total de disponibles** visible al elegir tareas.
- Al asignar, la lista de tareas se **refresca** (las que quedan en 0
  desaparecen) y se muestra un **resumen** de lo asignado.
- **Link de Trello obligatorio** y con recuadro visible.
- **Carga por archivo con upsert (clave = número de dosímetro):** sin asignación
  previa se crea, si algún dato cambió se actualiza y si es idéntica se deja
  igual. Cliente, ejecutivo y empresa se indican **por nombre**.

### Qué cumple

Cubre desde la asignación de cientos de dosímetros a la vez hasta el ajuste de
uno solo, y la carga desde una planilla ya preparada.

## 2.4 Dosímetros

**Acceso:** Administrador y Operador. *(Antes se llamaba "Buscar dosímetro".)*

**Propósito.** Consultar y **editar** un dosímetro puntual, y **corregir en
lote** asignaciones ya hechas.

### Qué se ve en pantalla

Dos pestañas:

- **Edición individual:** se busca por número y se ve el detalle del dosímetro y
  su **historial de asignaciones**. Permite:
  - Editar sus especificaciones (número, tipo, porta, observación) **sin** tocar
    tarea, bandeja ni slot.
  - Cambiar su estado: marcar dañado / bueno, marcar duplicado (stock de
    emergencia), liberar o dar de baja.
- **Correcciones masivas:** se filtran asignaciones (por cliente, ejecutivo,
  empresa, trimestre o link), se seleccionan varias y se **corrige un mismo
  campo** a la vez (link de Trello, cliente, ejecutivo, empresa o tipo de porta).

### Qué cumple

Resuelve dos necesidades típicas: arreglar los datos de un dosímetro concreto y
corregir un error repetido en muchas asignaciones (por ejemplo, cambiar el link
de Trello de 50 asignaciones de una sola vez).

## 2.5 Pendiente de envío

**Acceso:** Administrador y Operador (el Ejecutivo tiene su propia versión, solo
lectura — ver sección 3).

**Propósito.** Controlar el **despacho físico**: qué dosímetros ya están
asignados pero todavía no se han enviado.

### Qué se ve en pantalla

- Pestañas **Pendientes** y **Enviados**.
- **Filtros:** buscador por número de dosímetro, filtro por ejecutivo, chips de
  **trimestre** (multiselección) y **botoneras de orden** (por trimestre, fecha,
  cliente o número, ascendente/descendente).
- Tabla con **paginación de 10** por página; en "Enviados" se muestra además la
  fecha de envío.

### Funcionalidades

- Ver lo pendiente de despacho y **marcar como enviado** en lote (sale de la
  lista).
- Pestaña de **Enviados** para consultar lo ya despachado y, si hace falta,
  **revertir a pendiente**.
- Búsqueda, filtros y ordenamientos combinables.

## 2.6 Clientes

**Acceso:** Administrador y Operador.

**Propósito.** Gestionar la cartera de clientes y consultar su situación de
asignación.

### Qué se ve en pantalla

- **Alta de cliente** (razón social, nombre fantasía y ejecutivo responsable).
- **Filtros:** buscador **en vivo** (razón social o fantasía), por ejecutivo y
  por empresa.
- **Tabla de clientes** con su responsable y un estado (Con dosímetros /
  Pendiente de asignación). Al hacer clic en un cliente se abre su **detalle**
  con las asignaciones.

### Funcionalidades

- Crear y desactivar clientes.
- Búsqueda en vivo mientras se escribe, más filtros por ejecutivo y por empresa.
- Ver el detalle de asignaciones de cada cliente.

## 2.7 Ejecutivos

**Acceso:** Administrador y Operador.

**Propósito.** Administrar los ejecutivos (responsables comerciales) a los que se
asocian clientes y usuarios.

### Qué se ve en pantalla

- **Alta de ejecutivo** (nombre y email).
- **Tarjetas** de cada ejecutivo con sus iniciales, email y estado
  (Activo/Inactivo), con opción de **desactivar**.

### Qué cumple

Mantiene la lista de ejecutivos que luego se usa para asignar clientes, filtrar
y dar acceso a usuarios con rol Ejecutivo.

## 2.8 Duplicados

**Acceso:** Administrador y Operador (la acción de marcar la ejecuta el
Administrador).

**Propósito.** Detectar dosímetros cuyo **número físico está repetido** y
gestionarlos.

### Qué se ve en pantalla

- Un bloque por cada número repetido, con la lista de dosímetros que lo
  comparten (id interno único, tipo, estado y observación).
- El Administrador ve el botón **"Marcar emergencia"** para señalar un duplicado
  como stock de emergencia.

### Qué cumple

Da visibilidad y control sobre los números repetidos (habituales al reutilizar o
al tener stock de respaldo), evitando confusiones en el inventario.

## 2.9 Tipos de porta

**Acceso:** Administrador y Operador.

**Propósito.** Mantener el catálogo de **tipos de porta**, cada uno compatible
con un tipo de dosímetro (OSL, TLD o Cristal).

### Qué se ve en pantalla

- **Alta/edición** de un tipo de porta (nombre y tipo de dosímetro compatible).
- **Tabla** de portas con su compatibilidad, y acciones de editar y eliminar.

### Notas

- La compatibilidad porta–tipo es la que valida el sistema al armar y asignar.
- Un tipo de porta **no se puede eliminar si está en uso**.

---

# 3. Módulos de Ejecutivo

El ejecutivo tiene una vista **de solo consulta**, siempre acotada a su propia
cartera (sus clientes y sus dosímetros). Esa restricción se debe al ejecutivo
asociado a su usuario.

## 3.1 Pendiente de envío (mi vista)

**Acceso:** Ejecutivo (solo sus asignaciones).

**Propósito.** Que el ejecutivo vea qué dosímetros suyos están **pendientes de
envío** y cuáles ya fueron **enviados**.

### Qué se ve en pantalla

- Pestañas **Pendientes** y **Enviados**.
- Buscador por número de dosímetro, chips de trimestre y botoneras de orden.
- Tabla con paginación de 10 (sin acciones de marcado: es solo consulta).

### Qué cumple

Le da al ejecutivo visibilidad del estado de despacho de su cartera, sin poder
modificarlo (el despacho lo gestionan Administrador/Operador).

## 3.2 Mis dosímetros

**Acceso:** Ejecutivo.

**Propósito.** Ver los dosímetros asignados a sus clientes, **agrupados por
lote** (trimestre y fecha de asignación).

### Qué se ve en pantalla

- Un bloque por cada lote (trimestre — fecha, con la cantidad), y dentro la
  tabla de dosímetros: número, cliente, empresa, porta y link de Trello.

### Qué cumple

Permite al ejecutivo revisar rápidamente qué se le entregó a cada cliente en
cada envío.

## 3.3 Mis asignaciones

**Acceso:** Ejecutivo.

**Propósito.** Consultar en detalle todas sus asignaciones, con filtros y
exportación.

### Qué se ve en pantalla

- **Filtros:** cliente, trimestre, tipo de porta, fecha, número de bandeja,
  slot y link.
- **Tabla** con número de dosímetro, cliente, empresa, trimestre, fecha, porta,
  **tarea** y bandeja/slot.
- Botón **Exportar a Excel**.

### Funcionalidades

- Filtrar sus asignaciones por múltiples criterios.
- **Exportar a Excel** el resultado filtrado (incluye la tarea).

## 3.4 Mis clientes

**Acceso:** Ejecutivo.

**Propósito.** Ver su cartera de clientes y cuáles están pendientes de
asignación.

### Qué se ve en pantalla

- Un contador de cuántos clientes están pendientes de asignación.
- **Tabla** con razón social, nombre fantasía y estado (Con dosímetros /
  Pendiente de asignación).

### Qué cumple

Ayuda al ejecutivo a detectar de un vistazo qué clientes suyos aún no tienen
dosímetros asignados.

---

*Fin del manual.*
