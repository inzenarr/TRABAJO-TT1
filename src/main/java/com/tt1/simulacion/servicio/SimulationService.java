package com.tt1.simulacion.servicio;

import com.tt1.simulacion.dto.SolicitudDto;
import com.tt1.simulacion.modelo.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

    /**Numero de pasos que durará cada simulación. */
    static final int PASOS = 5;
    private static final Random RANDOM = new Random();

    /** Contador autoincremental para generar tokens únicos*/
    private int contador = 1;

    /** Mapa que contiene el nombre de usuario relacionado con su objeto Cliente */
    private final Map<String, Cliente> clientes = new ConcurrentHashMap<>();

    /**Mapa que relaciona un token de simulación con el resultado en formato de texto. */
    private final Map<Integer, String> resultados = new ConcurrentHashMap<>();

    /**
     * Solicita la creación y ejecución de una nueva simulación basada en los parámetros indicados.
     *
     * @param nombreUsuario Nombre del usuario que hace la solicitud.
     * @param solicitud Objeto de transferencia de datos que contiene la configuración
     * de la simulación, como los nombres de las criaturas y sus cantidades iniciales.
     * @return Token asignado a la simulación generada.
     */
    @Override
    public int solicitar(String nombreUsuario, SolicitudDto solicitud) {
        //creamos y ejecutamos la simulacion de las criaturas
        List<Criatura> criaturas = crearCriaturas(solicitud);
        String resultado = simular(criaturas);

        int token = contador;
        contador = contador + 1;

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
    public List<Integer> getTokenUsuario(String usuario) {
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
    public String getResultado(int token) {
        return resultados.get(token);
    }

    //CONSTRUCCION CRIATURAS
    private List<Criatura> crearCriaturas(SolicitudDto solicitud) {
        List<Criatura> criaturas = new ArrayList<>();
        List<String> nombres = solicitud.getNombreCriaturas();
        List<Integer> cantidades = solicitud.getCantidadesIniciales();

        for (int i = 0; i < nombres.size(); i++) {
            int cantidad = cantidades.get(i);
            String nombre = nombres.get(i).toLowerCase();
            for (int j = 0; j < cantidad; j++) {
                int x = RANDOM.nextInt(ANCHO_TABLERO);
                int y = RANDOM.nextInt(ANCHO_TABLERO);
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

        for (int t = 0; t < PASOS; t++) {
            List<Criatura> siguiente = new ArrayList<>();
            for (Criatura c : actual) {
                sb.append(t).append(",").append(c.getY()).append(",").append(c.getX()).append(",").append(getColor(c)).append("\n");
                siguiente.addAll(handleComportamiento(c));
            }
            actual = siguiente;
        }
        return sb.toString().trim();
    }

    //COLORES CRIATURA
    String getColor(Criatura c) {
        if (c instanceof Alpha) return "rojo";
        if (c instanceof Beta)  return "azul";
        if (c instanceof Gamma) return "verde";
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

        //gamma en cada iteracion se propaga en todas las direcciones, es decir el cuadrado crece +1 para todos lados
        if (c instanceof Gamma) {
            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            List<Criatura> resultado = new ArrayList<>();
            resultado.add(new Gamma(c.getX(), c.getY()));
            for (int[] dir : dirs) {
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