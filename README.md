# TRABAJO-TT1

El objetivo de este trabajo es crear una API la cual añadir a nuestras partes individuales en vez de usar el swagger.

---

## Modelo de dominio

El modelo sigue un **diseño anémico**: las clases solo almacenan datos (posición). Todo el comportamiento vive en `SimulacionService`.

### Tablero

Cuadrícula de **10 × 10** celdas. La esquina superior-izquierda es `(0, 0)`. `x` representa la columna e `y` la fila.

### Jerarquía

```
Criatura  (abstracta)
├── Alpha
├── Beta
└── Gamma
```

`Criatura` expone únicamente `getX()` y `getY()`. Sus atributos son `final`; cada paso de simulación crea nuevas instancias en lugar de mutar las existentes.

### Tipos de criatura

| Clase   | Comportamiento por paso                                              | Color |
|---------|----------------------------------------------------------------------|-------|
| `Alpha` | Permanece inmóvil.                                                   | Rojo  |
| `Beta`  | Se desplaza 1 celda en dirección aleatoria (norte/sur/este/oeste). Si saldría del tablero, se queda en el borde. | Azul  |
| `Gamma` | Se queda en su celda y genera una copia en cada celda adyacente dentro del tablero. Produce entre 3 y 5 criaturas según su posición (esquina / borde / centro). | Verde |

### Constantes (`SimulacionService`)

| Constante       | Valor | Descripción                        |
|-----------------|-------|------------------------------------|
| `ANCHO_TABLERO` | 10    | Número de columnas y filas.        |
| `PASOS`         | 5     | Iteraciones por simulación.        |

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
| `POST` | `/Solicitud/Solicitar?nombreUsuario=` | Lanza una simulación y devuelve un token. |
| `GET`  | `/Solicitud/GetSolicitudesUsuario?nombreUsuario=` | Lista los tokens del usuario. |
| `GET`  | `/Solicitud/ComprobarSolicitud?nombreUsuario=&tok=` | Comprueba si un token existe. |
| `POST` | `/Resultados?nombreUsuario=&tok=` | Devuelve los datos de la simulación. |
| `POST` | `/Email?emailAddress=&message=` | Envía un mensaje de correo. |

---

## Tecnología

- Java 17 · Spring Boot 3.4.4 · Maven
- Puerto: `8080`
- Tests: JUnit 5
