# Manual de Usuario — Simulación TT1

## Índice

1. [Requisitos previos](#1-requisitos-previos)
2. [Arrancar la aplicación](#2-arrancar-la-aplicación)
3. [Parar la aplicación](#3-parar-la-aplicación)
4. [Usar el frontend web](#4-usar-el-frontend-web)
5. [Usar la API directamente](#5-usar-la-api-directamente)
6. [Configurar colores y probabilidades](#6-configurar-colores-y-probabilidades)
7. [Ejecutar los tests](#7-ejecutar-los-tests)
8. [Cómo funciona la simulación](#8-cómo-funciona-la-simulación)
9. [Solución de problemas](#9-solución-de-problemas)

---

## 1. Requisitos previos

| Herramienta | Versión mínima | Para qué se usa |
|-------------|---------------|-----------------|
| **Docker Desktop** | 4.x | Ejecutar los contenedores |
| **Maven** | 3.9.x | Compilar el proyecto (incluido en `C:\Users\Daviti\.maven\`) |
| **Java JDK** | 17 | Compilación (no necesario si solo usas Docker) |

> **Importante:** Docker Desktop debe estar **en ejecución** antes de arrancar la aplicación.

---

## 2. Arrancar la aplicación

### Opción A — Script automático (recomendado)

Haz doble clic en `arrancar.ps1` o ejecútalo desde PowerShell:

```powershell
.\arrancar.ps1
```

El script compila el proyecto, construye las imágenes Docker y arranca ambos contenedores.

### Opción B — Manual

```powershell
# Desde la carpeta del proyecto TRABAJO-TT1
cd C:\Users\Daviti\Documents\GitHub\TRABAJO-TT1

# 1. Compilar el JAR
& "C:\Users\Daviti\.maven\maven-3.9.15\bin\mvn.cmd" package -DskipTests

# 2. Construir imágenes y arrancar contenedores
docker compose up --build -d
```

### Verificar que está funcionando

Una vez arrancado, comprueba que los dos contenedores están activos:

```powershell
docker compose ps
```

Deberías ver dos contenedores con estado `running`:
- `trabajo-tt1-simulacion-1` → API en el puerto **5000**
- `trabajo-tt1-frontend-1` → Frontend en el puerto **8081**

### URLs de acceso

| Servicio | URL |
|----------|-----|
| Frontend (interfaz web) | http://localhost:8081/solicitud |
| API REST (Swagger UI) | http://localhost:5000/swagger-ui/index.html |

---

## 3. Parar la aplicación

```powershell
cd C:\Users\Daviti\Documents\GitHub\TRABAJO-TT1
docker compose down
```

---

## 4. Usar el frontend web

Abre http://localhost:8081/solicitud en el navegador.

### Paso a paso

**1. Rellenar el formulario**

| Campo | Descripción | Ejemplo |
|-------|-------------|---------|
| Nombre de usuario | Identificador para guardar tus simulaciones | `usuario1` |
| Tipo de criatura | Selecciona `alpha`, `beta` o `gamma` | `alpha` |
| Cantidad | Número de criaturas de ese tipo | `2` |

Puedes añadir varios tipos de criaturas pulsando **"Añadir criatura"**.

**2. Lanzar la simulación**

Pulsa **"Simular"**. La API ejecuta la simulación y devuelve un token.

**3. Ver el resultado**

Se muestra un tablero de 10×10 con la evolución paso a paso. Usa el **slider** para navegar entre los pasos (0 al 4).

Cada color en el tablero corresponde a un tipo de criatura (configurables en `application.properties`):
- **Rojo** (`red`) → Alpha
- **Azul** (`blue`) → Beta
- **Verde** (`green`) → Gamma
- **Gris** (`#cccccc`) → celda vacía

**4. Historial**

Puedes consultar simulaciones anteriores introduciendo tu nombre de usuario y el token recibido.

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
  "token": 1,
  "error": null,
  "valid": true
}
```

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

**Respuesta:** lista de tokens `[1, 2, 3]`

**Ejemplo:**
```bash
curl "http://localhost:5000/Solicitud/GetSolicitudesUsuario?nombreUsuario=usuario1"
```

---

#### `POST /Resultados` — Obtener resultado de una simulación

**Parámetros de query:**
- `nombreUsuario` (string)
- `tok` (int) — token devuelto al crear la simulación

**Respuesta:**
```json
{
  "done": true,
  "token": 1,
  "error": null,
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
curl -X POST "http://localhost:5000/Resultados?nombreUsuario=usuario1&tok=1"
```

---

#### `GET /Solicitud/ComprobarSolicitud` — Comprobar si existe un token

**Parámetros de query:**
- `nombreUsuario` (string)
- `tok` (int)

Devuelve `[tok]` si existe o `[]` si no.

---

#### `POST /Email` — Envío de email (stub)

Endpoint disponible pero no implementado. Devuelve siempre `{"done": true}`.

---

## 6. Configurar colores y probabilidades

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
cd C:\Users\Daviti\Documents\GitHub\TRABAJO-TT1
& "C:\Users\Daviti\.maven\maven-3.9.15\bin\mvn.cmd" test
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
- Duración: **5 pasos** (0 al 4)
- Las criaturas se colocan en posiciones únicas aleatorias al inicio

### Tipos de criaturas

#### Alpha (rojo por defecto)
- **Permanece inmóvil** en su posición durante toda la simulación
- Bloquea la casilla que ocupa: ninguna otra criatura puede entrar

#### Beta (azul por defecto)
- Se **mueve una casilla** en cada paso (arriba, abajo, izquierda o derecha, 25% cada dirección)
- Si la casilla de destino está ocupada, **permanece** donde está
- No puede salir del tablero (rebota en los bordes)

#### Gamma (verde por defecto)
- **Permanece** en su posición (no se mueve)
- Con probabilidad **1/N** (configurable) intenta **generar un hijo** en una casilla adyacente aleatoria
- Si la casilla elegida está ocupada, el nacimiento se aborta ese paso
- El hijo es independiente y se comporta exactamente igual que el padre

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
