# Manual de Usuario — Simulación TT1

**Producto:** API Motor de Simulación de Criaturas

**Versión:** 1.0.0

**Desarrollado por:** Daviti, Jose e Iñigo

**Fecha:** Mayo 2026

---

## Página del título

Motor de Simulación Automática de Ecosistemas (API Backend)

Un servicio basado en Java y Spring Boot para la gestión, simulación y persistencia en memoria de entidades biológicas digitales (Alpha, Beta y Gamma) sobre tableros bidimensionales.

---

## Aviso de edición y Derechos de autor

© 2026. Todos los derechos reservados.
Queda prohibida la reproducción total o parcial de este manual, así como su distribución, sin el consentimiento expreso del autor. Este documento ha sido elaborado como parte del proyecto de la asignatura Taller Transversal I.

---

## Términos y condiciones / Descargos de responsabilidad

Este software está diseñado con fines educativos y de simulación algorítmica. El autor no se hace responsable del uso de esta API en entornos de producción críticos. La persistencia de los datos (Tokens) se realiza en memoria volátil; los datos se perderán si el servidor se reinicia.

Este software no está homologado para su uso en instalaciones nucleares, sistemas de armamento, dispositivos médicos de soporte vital, navegación aérea ni en cualquier otro entorno donde un fallo del software pueda provocar daños físicos, personales o medioambientales graves.

---

## Marcas registradas

Java es una marca registrada de Oracle Corporation.
Docker es una marca registrada de Docker, Inc.

---

## Garantías

El software se proporciona "tal cual", sin garantía de ningún tipo, expresa o implícita, incluyendo pero no limitándose a garantías de comerciabilidad o idoneidad para un propósito particular.

---

## Acuerdos de licencia

Distribuido bajo la Licencia MIT. Se permite el uso, copia y modificación del software de forma gratuita, siempre que se incluya el aviso de copyright original.

---

## Índice

1. [Requisitos previos](#1-requisitos-previos)
2. [Arrancar la aplicación](#2-arrancar-la-aplicación)
3. [Parar la aplicación](#3-parar-la-aplicación)
4. [Usar el frontend web](#4-usar-el-frontend-web)
5. [Usar la API directamente](#5-usar-la-api-directamente)
6. [Configurar la simulación](#6-configurar-la-simulación)
7. [Ejecutar los tests](#7-ejecutar-los-tests)
8. [Cómo funciona la simulación](#8-cómo-funciona-la-simulación)
9. [Solución de problemas](#9-solución-de-problemas)

---

## 1. Requisitos previos

| Herramienta | Versión mínima | Para qué se usa |
|-------------|---------------|-----------------|
| **Docker Desktop** | 4.x | Ejecutar los contenedores |
| **Maven** | 3.9.x | Compilar el proyecto |
| **Java JDK** | 17 | Compilación (no necesario si solo usas Docker) |

> **Importante:** Docker Desktop debe estar **en ejecución** antes de arrancar la aplicación.

---

## 2. Arrancar la aplicación

### Opción A — Script automático (recomendado)

Haz doble clic en `arrancar.ps1` o ejecútalo desde PowerShell:

```powershell
.\arrancar.ps1
```

El script detecta automáticamente Maven, compila el backend, construye la imagen Docker y arranca el contenedor de la API.

#### Integrar tu frontend (opcional)

Si tienes un proyecto frontend compatible, copia `.env.example` a `.env` y rellena la ruta:

```
# .env
FRONTEND_PATH=../mi-proyecto-frontend
```

Al ejecutar el script, compilará también el frontend y lo levantará en el puerto **8081**.

> El archivo `.env` es personal — no lo subas al repositorio.

### Opción B — Manual

```powershell
# Desde la carpeta del proyecto TRABAJO-TT1

# 1. Compilar el JAR del backend
mvn package -DskipTests

# 2. Construir imagen y arrancar contenedor
docker compose up --build -d
```

### Verificar que está funcionando

```powershell
docker compose ps
```

Deberías ver el contenedor `trabajo-tt1-simulacion-1` con estado `running`.

### URLs de acceso

| Servicio | URL |
|----------|-----|
| API REST (Swagger UI) | http://localhost:5000/swagger-ui/index.html |
| Frontend — formulario de solicitud | http://localhost:8081/solicitud |
| Ver grid con token (frontend) | http://localhost:8081/grid?tok={uuid} |

---

## 3. Parar la aplicación

```powershell
cd TRABAJO-TT1
docker compose down
```

---

## 4. Usar el frontend web

Abre http://localhost:8081/solicitud en el navegador.

### Paso a paso

**1. Rellenar el formulario**

Verás tres campos numéricos, uno por tipo de criatura:

| Campo | Descripción | Ejemplo |
|-------|-------------|---------|
| Num. de Alpha | Cantidad de criaturas Alpha a colocar | `2` |
| Num. de Beta | Cantidad de criaturas Beta a colocar | `1` |
| Num. de Gamma | Cantidad de criaturas Gamma a colocar | `1` |

Introduce `0` en los tipos que no quieras incluir.

**2. Lanzar la simulación**

Pulsa **"Solicitar"**. La API ejecuta la simulación y devuelve un token UUID.

**3. Ver el resultado**

Se muestra un tablero de 10×10 con la evolución paso a paso.

Cada color en el tablero corresponde a un tipo de criatura (configurables en `application.properties`):
- **Rojo** (`red`) → Alpha
- **Azul** (`blue`) → Beta
- **Verde** (`green`) → Gamma
- **Gris** (`#cccccc`) → celda vacía

---

## 5. Usar la API directamente

La API REST está disponible en http://localhost:5000. Puedes explorarla visualmente en el **Swagger UI**: http://localhost:5000/swagger-ui/index.html

### Endpoints disponibles

#### `POST /Solicitud/Solicitar` — Crear una simulación

**Parámetros de query:**
- `nombreUsuario` (string) — nombre del usuario

**Body (JSON):**
```json
{
  "nombreCriaturas": ["alpha", "beta", "gamma"],
  "cantidadesIniciales": [2, 1, 1]
}
```

**Respuesta:**
```json
{
  "done": true,
  "tokenSolicitud": "9bca07ee-784a-42b0-ac23-9c53782af06a",
  "errorMessage": null,
  "data": true
}
```

> El `tokenSolicitud` es un **UUID** (cadena larga). Guárdalo para consultar el resultado.

**Ejemplo con curl:**
```bash
curl -X POST "http://localhost:5000/Solicitud/Solicitar?nombreUsuario=usuario1" \
     -H "Content-Type: application/json" \
     -d '{"nombreCriaturas":["alpha","beta"],"cantidadesIniciales":[2,1]}'
```

---

#### `GET /Solicitud/GetSolicitudesUsuario` — Tokens de un usuario

**Parámetros de query:**
- `nombreUsuario` (string)

**Respuesta:** lista de UUID `["9bca07ee-784a-42b0-ac23-9c53782af06a", "..."]`

**Ejemplo:**
```bash
curl "http://localhost:5000/Solicitud/GetSolicitudesUsuario?nombreUsuario=usuario1"
```

---

#### `POST /Resultados` — Obtener resultado de una simulación

**Parámetros de query:**
- `nombreUsuario` (string)
- `tok` (string) — UUID devuelto al crear la simulación

**Respuesta:**
```json
{
  "done": true,
  "tokenSolicitud": "9bca07ee-784a-42b0-ac23-9c53782af06a",
  "errorMessage": null,
  "data": "10\n0,2,3,red\n0,7,1,blue\n..."
}
```

El campo `data` contiene el estado del tablero en formato texto:
```
<tamaño_tablero>
<paso>,<fila>,<columna>,<color>
...
```

**Ejemplo:**
```bash
curl -X POST "http://localhost:5000/Resultados?nombreUsuario=usuario1&tok=9bca07ee-784a-42b0-ac23-9c53782af06a"
```

---

#### `GET /Solicitud/ComprobarSolicitud` — Comprobar si existe un token

**Parámetros de query:**
- `nombreUsuario` (string)
- `tok` (string) — UUID

Devuelve `["<uuid>"]` si existe o `[]` si no.

---

#### `POST /Email` — Envío de email (stub)

Endpoint disponible pero no implementado. Devuelve siempre `{"done": true}`.

---

## 6. Configurar la simulación

Edita el archivo:
```
src/main/resources/application.properties
```

```properties
# Puerto del servidor
server.port=5000

# Colores de cada criatura (cualquier valor CSS válido)
simulacion.color.alpha=red
simulacion.color.beta=blue
simulacion.color.gamma=green

# Probabilidad de que Gamma genere un hijo por paso
# Valor N → probabilidad 1/N  (ej: 5 = 20%, 10 = 10%, 1 = 100%)
simulacion.gamma.prob.hijo=5

# Número de pasos (turnos) que dura cada simulación
simulacion.pasos=5

# Probabilidad (%) de que aparezca comida en cada casilla por turno
# Ej: 20 → 20% de probabilidad por casilla y turno
simulacion.comida.probabilidad=20

# Turnos consecutivos sin comer antes de que una criatura muera
simulacion.criatura.turnos.sin.comer=5
```

### Ejemplos de colores válidos

| Formato | Ejemplo |
|---------|---------|
| Nombre CSS | `red`, `blue`, `green`, `orange`, `purple` |
| Hexadecimal | `#FF0000`, `#0000FF` |
| RGB | `rgb(255, 0, 0)` |

### Aplicar los cambios

Tras modificar el archivo, hay que recompilar y reiniciar:

```powershell
.\arrancar.ps1
```

---

## 7. Ejecutar los tests

```powershell
cd TRABAJO-TT1
mvn test
```

El proyecto cuenta con **19 tests** distribuidos en:

| Clase de test | Tests | Qué verifica |
|---------------|-------|--------------|
| `SimulacionControllerTest` | 6 | Endpoints REST (con Spring context) |
| `Test_Alpha` | 2 | Coordenadas e instancia Alpha |
| `Test_Beta` | 2 | Coordenadas e instancia Beta |
| `Test_Gamma` | 2 | Coordenadas e instancia Gamma |
| `Test_Comportamiento_Criaturas` | 3 | Movimiento Alpha, Beta y Gamma |
| `Test_Simulacion_Servicio` | 4 | Lógica completa de simulación |

---

## 8. Cómo funciona la simulación

### El tablero

- Cuadrícula de **10×10** celdas
- Duración: configurable mediante `simulacion.pasos` (por defecto **5 pasos**, numerados del 0 al 4)
- Las criaturas se colocan en posiciones únicas aleatorias al inicio

### Mecánica de comida y hambre

En **cada turno**, cada casilla del tablero (tanto vacías como ocupadas) tiene una probabilidad configurable (por defecto **20%**) de generar comida.

- Si una criatura está en una casilla con comida, la **come** y su contador de hambre se reinicia a 0.
- Si no hay comida en su casilla, el contador de hambre sube en 1.
- Cuando el contador de hambre alcanza el límite configurable (por defecto **5 turnos sin comer**), la criatura **muere** y desaparece del tablero.
- La comida **desaparece** al ser consumida.

> Esto aplica a **todas** las criaturas: Alpha, Beta y Gamma.

### Tipos de criaturas

#### Alpha (rojo por defecto)
- **Permanece inmóvil** en su posición durante toda la simulación
- Bloquea la casilla que ocupa: ninguna otra criatura puede entrar
- Muere si no come durante el número de turnos configurado

#### Beta (azul por defecto)
- Se **mueve una casilla** en cada paso (arriba, abajo, izquierda o derecha, 25% cada dirección)
- Si la casilla de destino está ocupada, **permanece** donde está
- No puede salir del tablero (rebota en los bordes)
- Muere si no come durante el número de turnos configurado

#### Gamma (verde por defecto)
- **Permanece** en su posición (no se mueve)
- Con probabilidad **1/N** (configurable) intenta **generar un hijo** en una casilla adyacente aleatoria
- Si la casilla elegida está ocupada, el nacimiento se aborta ese paso
- El hijo nace con hambre = 0 (contador reiniciado)
- Muere si no come durante el número de turnos configurado

### Orden de resolución de colisiones por paso

1. **Alpha** se procesa primero (reserva su casilla)
2. **Beta** se mueve (respeta casillas de Alpha y de otros Beta ya movidos)
3. **Gamma** intenta generar hijos (respeta todas las casillas ya ocupadas o reservadas)

---

## 9. Solución de problemas

### "No se puede conectar a http://localhost:8081"
- Comprueba que Docker Desktop está en ejecución
- Ejecuta `docker compose ps` para ver el estado de los contenedores
- Revisa los logs: `docker compose logs frontend`

### "Error al compilar" / BUILD FAILURE
- Asegúrate de que Java 17 está instalado: `java -version`
- Asegúrate de que Docker Desktop está corriendo antes de ejecutar el script

### Los contenedores arrancan pero la simulación da error
- Revisa los logs de la API: `docker compose logs simulacion`
- Comprueba que `application.properties` tiene valores válidos en los campos de color

### Quiero reiniciar desde cero (borrar todos los datos)
Los datos de simulación se almacenan **en memoria** — al reiniciar los contenedores se pierden automáticamente:
```powershell
docker compose restart
```

### Ver logs en tiempo real
```powershell
docker compose logs -f
```
