# Kids Planner

Acceso al Código Fuente: El código completo del proyecto, incluyendo el historial de commits y versiones, se encuentra alojado públicamente en el siguiente repositorio de GitHub:

🔗 https://github.com/Antjrobles/kids-planner-app

**Aplicación Android para gestionar actividades extraescolares de manera familiar**

## Descripción

Kids Planner es una aplicación móvil para Android desarrollada en Kotlin que ayuda a las familias a coordinar las actividades extraescolares de sus hijos. Centraliza horarios, ubicaciones, materiales necesarios y pagos asociados, incorporando recordatorios locales para evitar olvidos.

**Características principales:**
- 📅 Planificación semanal de actividades
- 🎒 Checklist de materiales por actividad
- 💰 Seguimiento de pagos y cuotas
- 🔔 Recordatorios locales configurables
- 👨‍👩‍👧‍👦 Gestión de múltiples hijos y familias

## Tecnologías

- **Frontend:** Kotlin, Jetpack Compose, Material 3
- **Backend:** Supabase (PostgreSQL + REST API + Auth)
- **Herramientas:** Android Studio, Git, Gradle

## Requisitos Previos

- **JDK:** versión 11 o superior
- **Android Studio:** última versión estable con soporte Jetpack Compose
- **Android SDK:**
  - `minSdk`: 26 (Android 8.0)
  - `targetSdk` / `compileSdk`: 36
- **Cuenta Supabase:** para backend y base de datos (gratis en [supabase.com](https://supabase.com))

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/antjrobles/kidsplanner.git
cd kidsplanner
```

### 2. Configurar credenciales de Supabase

Crear el archivo `kids-planner-app/local.properties` con tus credenciales de Supabase:

```properties
SUPABASE_URL=https://tu-proyecto.supabase.co
SUPABASE_ANON_KEY=tu-anon-key
SUPABASE_SERVICE_ROLE_KEY=tu-service-role-key
```

> **⚠️ IMPORTANTE:** Nunca subas este archivo al repositorio. Ya está incluido en `.gitignore`.

**Dónde encontrar las credenciales:**
1. Accede a tu proyecto en [app.supabase.com](https://app.supabase.com)
2. Ve a Settings → API
3. Copia `URL`, `anon/public key` y `service_role key`

### 3. Configuración Adicional (`gradle.properties`)

El archivo `gradle.properties` se utiliza para configuraciones del build de Gradle que pueden variar entre entornos de desarrollo (por ejemplo, para ajustar el rendimiento de Gradle). Este archivo no debe ser incluido en el control de versiones.

Para configuraciones específicas de tu entorno, puedes crear un archivo `gradle.properties` en la raíz del proyecto.

**Ejemplo de `gradle.properties`:**

```properties
# Aumenta la memoria disponible para el proceso de Gradle
org.gradle.jvmargs=-Xmx4g

# Habilita el caché de configuración para acelerar los builds
org.gradle.caching=true
```

> **⚠️ IMPORTANTE:** Este archivo ya está ignorado en `.gitignore` para proteger configuraciones locales.

### 4. Configurar la base de datos

Ejecuta los scripts SQL en tu instancia de Supabase en el siguiente orden:

```bash
# En el SQL Editor de Supabase, ejecutar:
database/SQL_01_drops.sql      # Limpia tablas existentes (opcional)
database/SQL_02_tablas_base.sql    # Crea estructura base
database/SQL_03_actividades.sql    # Tablas de actividades
database/SQL_04_pagos_soporte.sql  # Tablas de pagos y soporte
```

O ejecuta el script completo:

```bash
database/SQL_supabase.sql  # Script completo con todas las tablas
```

### 5. Compilar el proyecto

Navega al directorio de la app y compila:

```bash
cd kids-planner-app
.\gradlew assembleDebug
```

### 6. Ejecutar la aplicación

**Opción A: Desde Android Studio**
1. Abre el proyecto en Android Studio
2. Espera a que Gradle sincronice las dependencias
3. Conecta un dispositivo Android o inicia un emulador
4. Haz clic en "Run" (▶️)

**Opción B: Desde línea de comandos**

```bash
# Instalar en dispositivo/emulador conectado
.\gradlew installDebug
```

## Comandos Útiles

Todos los comandos deben ejecutarse desde [kids-planner-app/](kids-planner-app/):

```bash
# Compilar APK de debug
.\gradlew assembleDebug

# Instalar en dispositivo
.\gradlew installDebug

# Ejecutar tests unitarios
.\gradlew testDebugUnitTest

# Ejecutar tests instrumentados (requiere dispositivo)
.\gradlew connectedDebugAndroidTest

# Ejecutar linter
.\gradlew lint

# Limpiar build
.\gradlew clean
```






## Contribuir

Este es un proyecto académico para el CFGS en Desarrollo de Aplicaciones Multiplataforma (DAM) - CIDEAD 2025-2026.

**Autor:** Antonio José Robles Muñoz

## Licencia

Proyecto académico - CIDEAD 2025-2026

---
