package com.tt1.simulacion.controlador;

import com.tt1.simulacion.dto.SolicitudDto;
import com.tt1.simulacion.servicio.ISimulacionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SimulacionController {

    private final ISimulacionService servicio;

    public SimulacionController(ISimulacionService servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/Email")
    @ResponseStatus(HttpStatus.CREATED)
    public EmailResponse email(@RequestParam String emailAddress,
                               @RequestParam String message) {
        return new EmailResponse(true, null);
    }

    @PostMapping("/Solicitud/Solicitar")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudResponse solicitar(@RequestParam String nombreUsuario,
                                       @RequestBody SolicitudDto solicitud) {
        int token = servicio.solicitar(nombreUsuario, solicitud);
        return new SolicitudResponse(true, token, null, true);
    }

    @GetMapping("/Solicitud/GetSolicitudesUsuario")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Integer> getSolicitudesUsuario(@RequestParam String nombreUsuario) {
        return servicio.getTokenUsuario(nombreUsuario);
    }

    @GetMapping("/Solicitud/ComprobarSolicitud")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Integer> comprobarSolicitud(@RequestParam String nombreUsuario,
                                            @RequestParam int tok) {
        String resultado = servicio.getResultado(tok);
        return resultado != null ? List.of(tok) : List.of();
    }

    @PostMapping("/Resultados")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultsResponse resultados(@RequestParam String nombreUsuario,
                                      @RequestParam int tok) {
        String data = servicio.getResultado(tok);
        return new ResultsResponse(true, tok, null, data);
    }

    // Con los records conseguimos generar mensajes de respuesta  (que Spring convierte automáticamente a JSON).
    // Generan de forma automática un constructor, getters, equals, hashCode y toString.
    //
    // Incluimos como parámetros:
    // - boolean done: Indica si la operación se completó con éxito o si falló.
    // - int tokenSolicitud: Identificador numérico único de la solicitud de simulación.
    // - String errorMessage: Mensaje descriptivo si ha ocurrido algún error durante la solicitud (null si done es true).
    // - boolean data: Indicador  del estado del proceso o de la validación de los datos.
    // - String data:  Cadena de texto que contiene el historial y estado final del tablero simulado.

    record EmailResponse(boolean done, String errorMessage) {}
    record SolicitudResponse(boolean done, int tokenSolicitud, String errorMessage, boolean data) {}
    record ResultsResponse(boolean done, int tokenSolicitud, String errorMessage, String data) {}
}
