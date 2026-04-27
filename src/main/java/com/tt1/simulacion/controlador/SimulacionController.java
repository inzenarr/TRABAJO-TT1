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

    record EmailResponse(boolean done, String errorMessage) {}
    record SolicitudResponse(boolean done, int tokenSolicitud, String errorMessage, boolean data) {}
    record ResultsResponse(boolean done, int tokenSolicitud, String errorMessage, String data) {}
}
