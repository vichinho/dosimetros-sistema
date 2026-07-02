# Migración del histórico (REPOSITORIO)

Genera un script SQL para cargar el histórico del Excel `REPOSITORIO`
(hoja con columnas `FECHA DE INGRESO, NUMDOSIM, EJECUTIVE, CLIENTE,
TRIMESTRE, FECHA DE ASIGNACION, TIPO DE PORTA, NUMERO DE BANDEJA,
SLOT DE BANDEJA, NUMERO DEL REPOSITORIO`) al esquema del sistema.

## Requisitos
```bash
pip install openpyxl
```
El backend ya debe haber corrido sus migraciones Flyway (V1/V2/V3), de modo
que los catálogos (empresa Photomat, tipos de dosímetro y portas base)
existan en la base.

## Uso
```bash
python3 generar_migracion.py datillos.xlsx migracion_repositorio.sql
```
Luego, en MySQL:
```bash
mysql -u root -p dosimetros_db < migracion_repositorio.sql
```

## Qué hace
- **Vacía** (TRUNCATE) las tablas `ASIGNACION`, `dosimetro`, `tarea`,
  `cliente` y `ejecutivo`. **No toca** los catálogos.
- Crea ejecutivos (Juan, Víctor, Alejandro + `Sin asignar` para filas sin
  ejecutivo), clientes, tareas, dosímetros (uno por número distinto) y las
  asignaciones.

## Supuestos (ajustables en el script)
- **Empresa**: todas las asignaciones quedan como **Photomat**.
- **Tipo de dosímetro**: se deduce del tipo de porta.
- **Portas nuevas** que no estaban en el catálogo se crean: `Sin armar`
  (TLD), `Cristales` (Cristal) y `OSL con broche` (OSL).
- **Estado del dosímetro**: `asignado` si su última fila tiene cliente; si
  no, `disponible`. La ubicación actual proviene de su última fila.
- **Filas sin cliente** no generan asignación (el dosímetro igual se crea).
- **Filas con cliente sin ejecutivo** usan el ejecutivo `Sin asignar`.
- **Filas sin trimestre o sin fecha de asignación** no generan asignación
  (ambos son obligatorios).
- `FECHA DE INGRESO` se guarda como `link_trello`.
- Filas con `NUMDOSIM` no numérico se descartan (se informan al final).

## Resultado de referencia (archivo de ejemplo)
- Ejecutivos: 4 · Clientes: 336 · Tareas: 166 · Dosímetros: 25.021 ·
  Asignaciones: 50.631 · descartadas: 3.
