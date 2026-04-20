package com.tt1.simulacion.servicio;

import com.tt1.simulacion.dto.SolicitudDto;
import com.tt1.simulacion.modelo.Alpha;
import com.tt1.simulacion.modelo.Beta;
import com.tt1.simulacion.modelo.Criatura;
import com.tt1.simulacion.modelo.Gamma;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class SimulationService implements ISimulacionService{
    static final int ANCHO_TABLERO = 10;
    static final int PASOS = 5;
    private static final Random RANDOM = new Random();
    private int contador = 1;
    private final Map<String, List<Integer>> tokensUsuario = new ConcurrentHashMap<>();
    private final Map<Integer, String> resultados = new ConcurrentHashMap<>();

    /*
        -Crea y ejecuta una simulacion con las criaturas solicitadas
        -Almacena el resultado y asigna el token al usuario
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

        List<Integer> listaDelUsuario = tokensUsuario.get(nombreUsuario);

        if (listaDelUsuario == null) {
            //si no existe la creamos
            listaDelUsuario = new ArrayList<>();
            tokensUsuario.put(nombreUsuario, listaDelUsuario);
        }

        //se añade el token a la lista
        listaDelUsuario.add(token);

        return token;
    }

    @Override
    public List<Integer> getTokenUsuario(String usuario) {
        return tokensUsuario.getOrDefault(usuario, List.of());
    }

    @Override
    public String getResultado(int token) {
        return resultados.get(token);
    }

    //CONSTRUCCION CRIATURAS
    private List<Criatura> crearCriaturas(SolicitudDto solicitud) {
        List<Criatura> criaturas = new ArrayList<>();
        List<String> nombres = solicitud.getNombreEntidades();
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
    List<Criatura> handleComportamiento(Criatura c) {
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
