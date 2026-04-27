package com.tt1.simulacion.servicio;

import com.tt1.simulacion.dto.SolicitudDto;

import java.util.List;


/**
 * Interfaz que define el servicio de simulaciones de criaturas.
 * <p>
 * Proporciona los métodos necesarios para iniciar nuevas simulaciones,
 * devolver los tokens de un usuario y recuperar los resultados obtenidos.
 * </p>
 */

public interface ISimulacionService {
    /**
     * Solicita la creación y ejecución de una nueva simulación basada en los parámetros indicados.
     *
     * @param nombreUsuario Nombre del usuario que hace la solicitud.
     * @param sd Objeto de transferencia de datos que contiene la configuración
     * de la simulación, como los nombres de las criaturas y sus cantidades iniciales.
     * @return Token asignado a la simulación generada.
     */
    int solicitar(String nombreUsuario, SolicitudDto sd);


    /**
     * Devuelve una lista completa de los tokens de un usuario
     *
     * @param usuario El nombre del usuario del cual queremos obtener los tokens.
     * @return lista de tokens del usuario.
     */
    List<Integer> getTokenUsuario(String usuario);

    /**
     * Devuelve el resultado obtenido al realizar una petición con un token.
     *
     * @param token numero de identificación de la solicitud
     * @return cadena de texto que representa el estado y evolución del tablero durante la evolución.
     */
    String getResultado(int token);
}
