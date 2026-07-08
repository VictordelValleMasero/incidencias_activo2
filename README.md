# Incidencias — Activo 2

Módulo de "Comunicar incidencia" integrado dentro de la app corporativa **Activo 2**, inspirado en el brief `Act2Inc.pdf` y en el proyecto hermano `QRIncidencias` (mismo stack: Java + Spring Boot en el backend, Angular en el frontend), pero **sin escaneo de QR**: el módulo se abre directamente al pulsar el apartado **Incidencias** que ya existe en la pantalla de inicio de la app.

Flujo de usuario:

```
Incidencias → Edificio (Jarrods / Campus) → Zona → Departamento → Descripción/Fotos → Enviar → WhatsApp al responsable
```

## Stack técnico

| Capa | Tecnología |
|---|---|
| Backend | Java 11 + Spring Boot 2.7 (Web, Security, Data JPA, Validation) |
| Base de datos | PostgreSQL 16 + Flyway |
| Autenticación | JWT (login de administrador; el usuario final no necesita login) |
| Frontend | Angular 18 (standalone components), TypeScript, SCSS |
| WhatsApp | Interfaz `WhatsappProvider` con implementación mock incluida; lista para conectar Meta Cloud API / Twilio |
| Almacenamiento de imágenes | Sistema de ficheros local (`backend/uploads`), abstraído tras `FileStorageService` |

## Estructura del proyecto

```
IncidenciasActivo2/
├── backend/            Proyecto Spring Boot (API REST)
├── frontend/            Proyecto Angular (home + módulo de incidencias + panel admin)
├── docker-compose.yml   PostgreSQL para desarrollo local
├── Act2Inc.pdf          Especificación funcional original
├── Interfaz.png         Referencia visual de la pantalla de inicio existente
└── Logo.jpg             Logo corporativo utilizado en la app
```

## Puesta en marcha (desarrollo local)

### Requisitos

- JDK 11 o superior (`JAVA_HOME` configurado)
- Node.js 18.19+ y npm
- Docker (recomendado) o una instancia de PostgreSQL 14+ propia

### 1. Base de datos

```bash
docker compose up -d
```

Levanta PostgreSQL en `localhost:5432` con la base `activo2incidencias` / usuario `activo2incidencias` / password `activo2incidencias`.

> **Nota (entorno actual sin Docker):** en esta máquina no hay Docker Desktop instalado, así que en vez del paso anterior se ha creado un clúster PostgreSQL 18 **independiente y propio del proyecto** (no toca el Postgres del sistema), usando los binarios ya instalados:
> - Directorio de datos: `C:\Users\Usuario\AppData\Local\activo2incidencias-pgdata`
> - Puerto: `5433` (para no chocar con el Postgres del sistema en 5432)
> - Usuario/BD/contraseña: `activo2incidencias` / `activo2incidencias` / `activo2incidencias`
>
> Arrancar: `"C:\Program Files\PostgreSQL\18\bin\pg_ctl.exe" -D "C:\Users\Usuario\AppData\Local\activo2incidencias-pgdata" -l activo2incidencias-pgdata.log start`
> Parar: `"C:\Program Files\PostgreSQL\18\bin\pg_ctl.exe" -D "C:\Users\Usuario\AppData\Local\activo2incidencias-pgdata" stop`
>
> Si más adelante se instala Docker Desktop, se puede migrar sin más al `docker compose up -d` de arriba (mismas credenciales, solo cambia el puerto a 5432 en `DB_URL`).

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run      # Windows: mvnw.cmd spring-boot:run
```

> **En este entorno** (Postgres propio en el puerto 5433, ver nota anterior), arráncalo así, con `JAVA_HOME` apuntando al JDK 11 instalado en `C:\Program Files\Java\jdk-11.0.0.1`:
> ```powershell
> $env:JAVA_HOME = "C:\Program Files\Java\jdk-11.0.0.1"
> $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
> $env:DB_URL = "jdbc:postgresql://localhost:5433/activo2incidencias"
> $env:DB_USER = "activo2incidencias"
> $env:DB_PASSWORD = "activo2incidencias"
> $env:WHATSAPP_PROVIDER = "mock"
> .\mvnw.cmd spring-boot:run
> ```

- Flyway crea el esquema y siembra datos de ejemplo: edificios **Jarrods** y **Campus**, sus zonas, los departamentos, responsables de demostración y las asociaciones zona/departamento/responsable.
- Administrador de demo (se crea automáticamente si no existe ninguno):
  - **Email:** `admin@activo2-demo.com`
  - **Contraseña:** `Admin123!`
- API en `http://localhost:8080/api`. Documentación OpenAPI en `http://localhost:8080/docs`.

Variables de entorno relevantes: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `APP_PUBLIC_URL`, `STORAGE_ROOT`, `WHATSAPP_PROVIDER`, `WHATSAPP_SENDER`.

### 3. Frontend

```bash
cd frontend
npm install
npm start          # ng serve, con proxy hacia el backend en :8080
```

Abre `http://localhost:4200`.

- `/` — pantalla de inicio de Activo 2 (réplica del diseño existente en `Interfaz.png`), con el apartado **Incidencias**.
- `/incidencias` — módulo público de "Comunicar incidencia" (sin login, mobile-first).
- `/admin` — panel de administración (requiere login).

### 4. Probar el flujo completo

1. Abre `http://localhost:4200` y pulsa el apartado **Incidencias**.
2. Elige edificio (Jarrods o Campus) → zona → departamento.
3. Escribe una descripción y, opcionalmente, adjunta fotos.
4. Pulsa **Enviar incidencia** y comprueba la pantalla de confirmación con el número de incidencia.
5. Entra en `http://localhost:4200/admin/login` con las credenciales de demo y revisa **Incidencias**: la incidencia aparece registrada, junto con el resultado del envío de WhatsApp (en modo demo el envío es simulado y queda registrado en el log del backend).

## WhatsApp: de mock a producción

El envío está detrás de la interfaz `WhatsappProvider` (`backend/src/main/java/.../service/whatsapp/`). Por defecto se usa `MockWhatsappProvider`, que simula el envío y permite probar todo el flujo sin credenciales reales. Para conectar un proveedor real (WhatsApp Business API / Meta Cloud API, Twilio, 360dialog, etc.), implementa `WhatsappProvider`, actívalo vía `WHATSAPP_PROVIDER` y añade las credenciales como variables de entorno — no hace falta tocar el resto del código.

## Reglas de diseño respetadas

- No se ha rediseñado la pantalla de inicio ni se ha tocado el logo/branding existente: `Interfaz.png` y `Logo.jpg` se han usado como referencia visual para reconstruir un shell de inicio fiel, y todo el desarrollo se ha concentrado en el módulo que se abre al pulsar **Incidencias**.
- Edificios, zonas, departamentos, responsables y sus asociaciones son completamente configurables desde el panel de administrador.
- El usuario final no necesita registrarse ni iniciar sesión; el administrador sí.
