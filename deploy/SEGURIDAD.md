# Guía de despliegue seguro

Checklist de seguridad para poner el sistema en producción.

## 1. Variables de entorno obligatorias
En producción define estas variables (nunca uses los valores por defecto):

```bash
export SPRING_PROFILES_ACTIVE=prod
export JWT_SECRET=<cadena aleatoria de 32+ caracteres>
export DB_URL=jdbc:mysql://<host>:3306/dosimetros_db
export DB_USERNAME=<usuario_bd>
export DB_PASSWORD=<contraseña fuerte>
export CORS_ALLOWED_ORIGINS=https://tu-dominio.cl
```

> Con `SPRING_PROFILES_ACTIVE=prod`, la aplicación **aborta el arranque** si
> `JWT_SECRET` o `DB_PASSWORD` siguen en su valor por defecto (ver
> `StartupSecurityCheck`).

Generar un secreto JWT aleatorio:
```bash
openssl rand -hex 32
```

## 2. Base de datos
- Usar un usuario MySQL **dedicado** con permisos solo sobre `dosimetros_db`
  (no `root`).
- No exponer el puerto 3306 a Internet; solo accesible desde el backend.

## 3. Usuario administrador
- Cambia la contraseña del admin semilla (`admin` / `admin123`) tras el primer
  ingreso, o crea un admin nuevo y desactiva el semilla.

## 4. HTTPS (Nginx)
- Termina TLS en Nginx (certificado válido, p. ej. Let's Encrypt) y redirige
  HTTP → HTTPS. Con HTTPS activo, el backend envía la cabecera HSTS.
- Nginx reenvía `/api` al backend y sirve el frontend (ver
  `deploy/nginx.conf.example`).
- Asegúrate de reenviar `X-Forwarded-For` para que el rate limiting del login
  identifique bien la IP del cliente.

## 5. CORS
- Define `CORS_ALLOWED_ORIGINS` con el/los dominios reales del frontend.
- Si frontend y API van detrás del mismo Nginx (mismo origen), puede quedar
  vacío.

## 6. Análisis de vulnerabilidades de dependencias
Ejecuta el chequeo OWASP (la primera vez descarga la base NVD):
```bash
./mvnw dependency-check:check
```
Falla si hay vulnerabilidades con CVSS ≥ 7. Intégralo en CI.

## 7. Controles ya incluidos en el sistema
- Contraseñas con BCrypt.
- Autenticación JWT firmada; autorización por rol en todos los endpoints.
- Rate limiting del login (5 intentos/15 min por IP → bloqueo 15 min).
- Cabeceras de seguridad (X-Frame-Options: DENY, X-Content-Type-Options,
  Referrer-Policy, HSTS bajo HTTPS).
- Límite de tamaño y de filas en la carga de Excel + mitigación de zip-bomb.
- Secretos externalizados a variables de entorno.

## 8. Recomendaciones adicionales (opcionales)
- Rotar el `JWT_SECRET` periódicamente.
- Copias de seguridad automáticas de la base de datos.
- Monitoreo/alertas de errores y accesos.
- Considerar refresh tokens con expiración de acceso más corta.
