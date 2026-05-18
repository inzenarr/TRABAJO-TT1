# TRABAJO-TT1

API REST de simulación de ecosistemas de criaturas, diseñada para integrarse con un frontend independiente.

---

## Modelo de dominio

El modelo sigue un **diseño anémico**: las clases solo almacenan datos. Todo el comportamiento vive en `SimulacionService`.

### Tablero

Cuadrícula de **10 × 10** celdas. La esquina superior-izquierda es `(0, 0)`. `x` representa la columna e `y` la fila.

### Jerarquía

```
Criatura  (abstracta)
├── Alpha
├── Beta
└── Gamma
```

`Criatura` expone `getX()`, `getY()` y `getHambre()`. Sus atributos son `final`; cada paso de simulación crea nuevas instancias en lugar de mutar las existentes.

### Tipos de criatura

| Clase   | Comportamiento por paso                                              | Color |
|---------|----------------------------------------------------------------------|-------|
| `Alpha` | Permanece inmóvil.                                                   | Rojo  |
| `Beta`  | Se desplaza 1 celda en dirección aleatoria (norte/sur/este/oeste). Si saldría del tablero, se queda en el borde. | Azul  |
| `Gamma` | Se queda en su celda y genera una copia en cada celda adyacente dentro del tablero. Produce entre 3 y 5 criaturas según su posición (esquina / borde / centro). | Verde |

### Mecánica de comida y hambre

Cada turno, cada celda tiene una probabilidad configurable de generar comida. Si una criatura ocupa una celda con comida, su contador de hambre se reinicia. Si una criatura supera el número máximo de turnos sin comer, muere y desaparece de la simulación.

### Configuración (`application.properties`)

| Propiedad | Valor por defecto | Descripción |
|-----------|-------------------|-------------|
| `simulacion.pasos` | `5` | Número de turnos por simulación |
| `simulacion.comida.probabilidad` | `20` | % de probabilidad de comida por celda y turno |
| `simulacion.criatura.turnos.sin.comer` | `5` | Turnos sin comer antes de morir |
| `simulacion.color.alpha` | `red` | Color de las Alpha |
| `simulacion.color.beta` | `blue` | Color de las Beta |
| `simulacion.color.gamma` | `green` | Color de las Gamma |
| `simulacion.gamma.prob.hijo` | `5` | Probabilidad de reproducción de Gamma |

### Formato de salida

La simulación devuelve un `String` con el siguiente formato:

```
10
paso,y,x,color
paso,y,x,color
...
```

La primera línea es el ancho del tablero. Cada línea siguiente representa una criatura en un instante dado.

---

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/Solicitud/Solicitar?nombreUsuario=` | Lanza una simulación y devuelve un token UUID. |
| `GET`  | `/Solicitud/GetSolicitudesUsuario?nombreUsuario=` | Lista los tokens del usuario. |
| `GET`  | `/Solicitud/ComprobarSolicitud?nombreUsuario=&tok=` | Comprueba si un token existe. |
| `POST` | `/Resultados?nombreUsuario=&tok=` | Devuelve los datos de la simulación. |
| `POST` | `/Email?emailAddress=&message=` | Envía un mensaje de correo. |

Los tokens son **UUID** (ej: `9bca07ee-784a-42b0-ac23-9c53782af06a`).

Documentación interactiva disponible en `http://localhost:5000/swagger-ui/index.html`.

---

## Arrancar la aplicación

### Opción A — Script automático (recomendado)

```powershell
.\arrancar.ps1
```

Compila backend y frontend, construye las imágenes Docker y arranca los contenedores.

### Opción B — Docker manual

```powershell
mvn package -DskipTests
docker compose up --build -d
```

---

## Tecnología

- Java 17 · Spring Boot 3.4.4 · Maven
- Puerto: `5000` (backend) · `8081` (frontend)
- Tests: JUnit 5 · 19 tests
