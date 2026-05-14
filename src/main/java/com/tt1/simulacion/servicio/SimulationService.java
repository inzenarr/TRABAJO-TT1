package com.tt1.simulacion.servicio;

import com.tt1.simulacion.dto.SolicitudDto;
import com.tt1.simulacion.modelo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio encargado de gestionar y ejecutar las simulaciones de criaturas.
 * <p>
 * Implementa {@link ISimulacionService} y maneja la lógica de creación,
 * comportamiento en el tablero y el almacenamiento de los resultados vinculados
 * a los usuarios mediante tokens de ejecución.
 * </p>
 */
@Service
public class SimulationService implements ISimulacionService{
    /** Define el ancho y alto del tablero. */
    static final int ANCHO_TABLERO = 10;

    private static final Random RANDOM = new Random();

    @Value("${simulacion.pasos:5}")
    private int pasos;

    @Value("${simulacion.color.alpha:red}")
    private String colorAlpha;

    @Value("${simulacion.color.beta:blue}")
    private String colorBeta;

    @Value("${simulacion.color.gamma:green}")
    private String colorGamma;

    @Value("${simulacion.gamma.prob.hijo:5}")
    private int gammaProbHijo;

    @Value("${simulacion.comida.probabilidad:20}")
    private int comidaProbabilidad;

    @Value("${simulacion.criatura.turnos.sin.comer:5}")
    private int maxTurnosSinComer;

    /** Constructor por defecto: Spring inyecta los @Value. Usado también en tests. */
    public SimulationService() {
        this.pasos              = 5;
        this.colorAlpha         = "red";
        this.colorBeta          = "blue";
        this.colorGamma         = "green";
        this.gammaProbHijo      = 5;
        this.comidaProbabilidad = 20;
        this.maxTurnosSinComer  = 5;
    }

    /** Contador autoincremental para generar tokens únicos*/
    // eliminado: token ahora es UUID

    /** Mapa que contiene el nombre de usuario relacionado con su objeto Cliente */
    private final Map<String, Cliente> clientes = new ConcurrentHashMap<>();

    /**Mapa que relaciona un token de simulación con el resultado en formato de texto. */
    private final Map<String, String> resultados = new ConcurrentHashMap<>();

    /**
     * Solicita la creación y ejecución de una nueva simulación basada en los parámetros indicados.
     *
     * @param nombreUsuario Nombre del usuario que hace la solicitud.
     * @param solicitud Objeto de transferencia de datos que contiene la configuración
     * de la simulación, como los nombres de las criaturas y sus cantidades iniciales.
     * @return Token asignado a la simulación generada.
     */
    @Override
    public String solicitar(String nombreUsuario, SolicitudDto solicitud) {
        //creamos y ejecutamos la simulacion de las criaturas
        List<Criatura> criaturas = crearCriaturas(solicitud);
        String resultado = simular(criaturas);

        String token = UUID.randomUUID().toString();

        //guardamos los resultados en el mapa
        resultados.put(token, resultado);

        // Buscamos al cliente o lo creamos si no existe, y añadimos el token
        Cliente cliente = clientes.get(nombreUsuario);
        if (cliente == null) {
            cliente = new Cliente(nombreUsuario);
            clientes.put(nombreUsuario, cliente);
        }
        cliente.addToken(token);

        return token;
    }


    /**
     * Devuelve una lista completa de los tokens de un usuario
     *
     * @param usuario El nombre del usuario del cual queremos obtener los tokens.
     * @return lista de tokens del usuario.
     */
    @Override
    public List<String> getTokenUsuario(String usuario) {
        Cliente cliente = clientes.get(usuario);
        return (cliente != null) ? cliente.getTokens() : List.of();
    }

    /**
     * Devuelve el resultado obtenido al realizar una petición con un token.
     *
     * @param token numero de identificación de la solicitud
     * @return cadena de texto que representa el estado y evolución del tablero durante la evolución.
     */

    @Override
    public String getResultado(String token) {
        return resultados.get(token);
    }

    //CONSTRUCCION CRIATURAS
    private List<Criatura> crearCriaturas(SolicitudDto solicitud) {
        List<Criatura> criaturas = new ArrayList<>();
        List<String> nombres = solicitud.getNombreCriaturas();
        List<Integer> cantidades = solicitud.getCantidadesIniciales();
        Set<String> ocupadas = new HashSet<>();

        for (int i = 0; i < nombres.size(); i++) {
            int cantidad = cantidades.get(i);
            String nombre = nombres.get(i).toLowerCase();
            for (int j = 0; j < cantidad; j++) {
                int x, y;
                do {
                    x = RANDOM.nextInt(ANCHO_TABLERO);
                    y = RANDOM.nextInt(ANCHO_TABLERO);
                } while (!ocupadas.add(x + "," + y));
                criaturas.add(switch (nombre) {
                    case "alpha" -> new Alpha(x, y);
                    case "beta"  -> new Beta(x, y);
                    case "gamma" -> new Gamma(x, y);
                    default      -> new Alpha(x, y);

                });
            }
        }
        return criaturas;
    }

    //SIMULACION CRIATURAS
    String simular(List<Criatura> criaturas) {
        StringBuilder sb = new StringBuilder();
        sb.append(ANCHO_TABLERO).append("\n");
        List<Criatura> actual = new ArrayList<>(criaturas);

        for (int t = 0; t < pasos; t++) {
            for (Criatura c : actual) {
                sb.append(t).append(",").append(c.getY()).append(",").append(c.getX()).append(",").append(getColor(c)).append("\n");
            }

            // Generar comida aleatoria en cada casilla (prob configurable)
            Set<String> comidasHoy = new HashSet<>();
            for (int cx = 0; cx < ANCHO_TABLERO; cx++) {
                for (int cy = 0; cy < ANCHO_TABLERO; cy++) {
                    if (RANDOM.nextInt(100) < comidaProbabilidad) {
                        comidasHoy.add(cx + "," + cy);
                    }
                }
            }

            // Posiciones ocupadas al inicio del paso
            Set<String> ocupadas = new HashSet<>();
            for (Criatura c : actual) {
                ocupadas.add(c.getX() + "," + c.getY());
            }

            Set<String> destinosReservados = new HashSet<>();
            List<Criatura> siguiente = new ArrayList<>();

            // PASADA 1: mover Alpha y Beta primero
            for (Criatura c : actual) {
                if (c instanceof Alpha) {
                    String pos = c.getX() + "," + c.getY();
                    boolean comio = comidasHoy.remove(pos);
                    int nuevoHambre = comio ? 0 : c.getHambre() + 1;
                    if (nuevoHambre < maxTurnosSinComer) {
                        siguiente.add(new Alpha(c.getX(), c.getY(), nuevoHambre));
                        destinosReservados.add(pos);
                    }

                } else if (c instanceof Beta) {
                    Criatura candidato = handleComportamiento(c).get(0);
                    String posC = candidato.getX() + "," + candidato.getY();
                    String posActual = c.getX() + "," + c.getY();
                    boolean libre = !ocupadas.contains(posC) && !destinosReservados.contains(posC);
                    String finalPos = (!posC.equals(posActual) && libre) ? posC : posActual;
                    boolean comio = comidasHoy.remove(finalPos);
                    int nuevoHambre = comio ? 0 : c.getHambre() + 1;
                    if (nuevoHambre < maxTurnosSinComer) {
                        destinosReservados.add(finalPos);
                        if (finalPos.equals(posActual)) {
                            siguiente.add(new Beta(c.getX(), c.getY(), nuevoHambre));
                        } else {
                            siguiente.add(new Beta(candidato.getX(), candidato.getY(), nuevoHambre));
                        }
                    }
                }
            }

            // PASADA 2: expandir Gamma usando posiciones finales de Alpha/Beta
            for (Criatura c : actual) {
                if (c instanceof Gamma) {
                    String posActual = c.getX() + "," + c.getY();
                    boolean comioPadre = comidasHoy.remove(posActual);
                    int nuevoHambrePadre = comioPadre ? 0 : c.getHambre() + 1;

                    if (nuevoHambrePadre < maxTurnosSinComer) {
                        siguiente.add(new Gamma(c.getX(), c.getY(), nuevoHambrePadre));
                        destinosReservados.add(posActual);

                        for (Criatura candidato : handleComportamiento(c)) {
                            String posC = candidato.getX() + "," + candidato.getY();
                            if (!posC.equals(posActual) && !ocupadas.contains(posC) && !destinosReservados.contains(posC)) {
                                destinosReservados.add(posC);
                                comidasHoy.remove(posC); // consume comida si la hay (hijo nace con hambre=0)
                                siguiente.add(new Gamma(candidato.getX(), candidato.getY(), 0));
                            }
                        }
                    }
                }
            }

            actual = siguiente;
        }
        return sb.toString().trim();
    }

    //COLORES CRIATURA
    String getColor(Criatura c) {
        if (c instanceof Alpha) return colorAlpha;
        if (c instanceof Beta)  return colorBeta;
        if (c instanceof Gamma) return colorGamma;
        return null;
    }

    //COMPORTAMIENTO CRIATURAS
    public List<Criatura> handleComportamiento(Criatura c) {
        //alpha en cada iteracion permanece donde esta
        if (c instanceof Alpha) {
            return List.of(new Alpha(c.getX(), c.getY()));
        }

        //beta en cada iteracion puede moverse hacia: arriba,abajo,izquierda y derecha
        if (c instanceof Beta) {
            int dir = RANDOM.nextInt(4);
            int[] dx = {-1, 1,  0, 0};
            int[] dy = { 0, 0, -1, 1};
            int nx = Math.max(0, Math.min(ANCHO_TABLERO - 1, c.getX() + dx[dir]));
            int ny = Math.max(0, Math.min(ANCHO_TABLERO - 1, c.getY() + dy[dir]));
            return List.of(new Beta(nx, ny));
        }

        //gamma permanece en su posicion; con prob 1/gammaProbHijo genera un hijo en una casilla adyacente aleatoria
        if (c instanceof Gamma) {
            List<Criatura> resultado = new ArrayList<>();
            resultado.add(new Gamma(c.getX(), c.getY()));
            if (RANDOM.nextInt(gammaProbHijo) == 0) {
                int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                int[] dir = dirs[RANDOM.nextInt(4)];
                int nx = c.getX() + dir[0];
                int ny = c.getY() + dir[1];
                if (nx >= 0 && nx < ANCHO_TABLERO && ny >= 0 && ny < ANCHO_TABLERO) {
                    resultado.add(new Gamma(nx, ny));
                }
            }
            return resultado;
        }
        return List.of(new Alpha(c.getX(), c.getY()));
    }
}