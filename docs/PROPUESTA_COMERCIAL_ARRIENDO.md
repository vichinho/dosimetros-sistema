# Propuesta comercial — Sistema de Gestión de Dosímetros (modelo arriendo/SaaS)

> Documento de trabajo con valores **estimativos** para el mercado chileno (CLP,
> sin IVA salvo que se indique). Los montos son referenciales y se ajustan según
> número de usuarios, nivel de soporte y configuración de hosting.

---

## 1. Arquitectura de hosting en AWS Lightsail

Lightsail es un VPS gestionado de AWS con **precio plano mensual**. Para un
sistema de una empresa (uso interno) es la opción más simple y económica.

### Componentes

```
                      Internet
                         │  (HTTPS 443)
                 ┌───────▼────────┐
                 │  Lightsail VPS │   Ubuntu · 4 GB RAM · 2 vCPU · 80 GB SSD
                 │                │   IP estática (gratis)
                 │  ┌──────────┐  │
                 │  │  Nginx   │  │   Sirve el frontend (React build) y
                 │  └────┬─────┘  │   reenvía /api al backend (reverse proxy)
                 │       │        │
                 │  ┌────▼─────┐  │
                 │  │ Backend  │  │   Spring Boot (.jar) como servicio systemd
                 │  │ :8080    │  │   (solo accesible localmente)
                 │  └────┬─────┘  │
                 │       │        │
                 │  ┌────▼─────┐  │
                 │  │ MySQL 8  │  │   En la misma instancia (solo local)
                 │  └──────────┘  │
                 └────────────────┘
                         │
                 Snapshots diarios (respaldo automático)
```

### Detalle de la configuración recomendada

| Elemento | Detalle |
|---|---|
| **Instancia** | Lightsail Ubuntu, **4 GB RAM / 2 vCPU / 80 GB SSD** (Java necesita RAM) |
| **Frontend** | Build de React servido por **Nginx** como estático |
| **Backend** | Spring Boot empaquetado (.jar), corriendo como servicio `systemd` en el puerto 8080 (interno) |
| **Base de datos** | **MySQL 8** en la misma instancia (o base gestionada aparte, ver opción B) |
| **HTTPS** | Certificado gratuito **Let's Encrypt** (Certbot + Nginx) |
| **Dominio / DNS** | Zona DNS de Lightsail (gratis) apuntando al dominio |
| **IP fija** | IP estática de Lightsail (gratis mientras esté asociada) |
| **Respaldos** | **Snapshots automáticos** diarios de la instancia |
| **Seguridad** | Firewall: 80/443 abiertos, SSH (22) restringido por IP, MySQL solo local |

### Opciones

- **Opción A (recomendada, todo-en-uno):** backend + MySQL + Nginx en una sola
  instancia de 4 GB. Simple y barato.
- **Opción B (BD separada):** instancia de 2 GB + **Lightsail Managed Database
  (MySQL)**. Más robusto para producción; algo más caro.
- **Escalar a futuro:** si crece (varios clientes, mucha carga, alta
  disponibilidad estricta), se migra a **EC2 + RDS**. Para uso interno,
  Lightsail alcanza por años.

### Costo mensual del hosting

| Recurso | USD/mes | CLP/mes (aprox.) |
|---|---|---|
| Instancia 4 GB (Opción A) | $24 | ~$23.000 |
| Snapshots / respaldos | $3 – $5 | ~$3.000 – $5.000 |
| IP fija + DNS | $0 | $0 |
| **Total Opción A** | **~$27 – $29** | **~$26.000 – $30.000** |
| *(Opción B: instancia 2 GB $12 + Managed DB 1 GB $15)* | ~$30 – $35 | ~$29.000 – $34.000 |

> El **montaje inicial** en Lightsail (instancia, MySQL, Nginx, HTTPS, dominio,
> respaldos) se cobra dentro de la **implementación única**, no como recurrente.

---

## 2. Propuesta de arriendo (con hosting incluido)

### A. Pago único inicial — Implementación / Onboarding

Despliegue en Lightsail, migración de la planilla histórica, configuración y
capacitación.

**$1.500.000 – $3.000.000** (recomendado **$2.000.000**).

### B. Suscripción mensual — planes

| Plan | Incluye | Precio/mes (CLP) |
|---|---|---|
| **Básico** | Hasta ~5 usuarios · hosting Lightsail + respaldos · soporte por email · correctivos | **$180.000 – $250.000** |
| **Profesional** ⭐ | Hasta ~15 usuarios · soporte prioritario · ~4–6 h/mes de mejoras menores · hosting + respaldos | **$350.000 – $450.000** |
| **Premium** | Usuarios ilimitados · SLA de respuesta · evolutivo (más horas) · integraciones (Power BI) | **$550.000 – $800.000** |

*Todos incluyen hosting en Lightsail, respaldos, disponibilidad, soporte y
mejoras menores. Los módulos nuevos grandes se cotizan aparte o por bolsa de
horas ($25.000 – $50.000/h).*

### C. Plan anual

Se cobra **11 meses** (2 gratis) o **~15–20% de descuento**. Se recomienda fijar
en **UF** para evitar desactualización.

### D. Condiciones

- Contrato mínimo **12 meses**, renovación automática.
- Reajuste anual por **IPC o UF**.
- Aviso de término **30–60 días**.
- Los valores **no incluyen IVA** (agregar +19%).

---

## 3. Plan recomendado y números

**Escenario típico:** 1 empresa, ~10 usuarios, hosting incluido (Lightsail
Opción A).

> **Implementación única: $2.000.000**
> **+ Plan Profesional: $400.000/mes** (hosting, respaldos, soporte y
> mantenimiento incluidos)
> Contrato 12 meses · en UF · + IVA

### Facturación

| Concepto | Monto |
|---|---|
| Primer año | $2.000.000 + ($400.000 × 12) = **$6.800.000** |
| Años siguientes (recurrente) | **$4.800.000 / año** |

### Margen

| Concepto | Monto/mes |
|---|---|
| Ingreso suscripción | $400.000 |
| − Hosting Lightsail | ~$30.000 |
| **= Margen bruto** (antes de tu tiempo de soporte) | **~$370.000** |

---

## 4. Factores que suben o bajan el precio

- **N° de usuarios / empresas** (una interna vs. multi-cliente).
- **Nivel de SLA** y horario de soporte.
- **Entrega de código fuente** (si se entrega, sube mucho — o pasa a modelo venta).
- **Exclusividad** frente a la competencia (premium).
- **Migración de datos** desde la planilla histórica y **capacitación**.
- **Configuración de hosting** (Opción A simple vs. Opción B con BD gestionada /
  alta disponibilidad).
