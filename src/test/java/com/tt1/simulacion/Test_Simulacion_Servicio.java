package com.tt1.simulacion;

import com.tt1.simulacion.dto.SolicitudDto;
import com.tt1.simulacion.servicio.SimulationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Test_Simulacion_Servicio {
    @Test
    void testSolicitudYRecuperacionDeResultado() {
        SolicitudDto dto = new SolicitudDto(List.of(1), List.of("alpha"));

        int token = SimulationService.solicitar("inzenarr", dto);

        String resultado = SimulationService.getResultado(token);

        assertNotNull(resultado, "El resultado debería estar guardado en el mapa");
        assertTrue(resultado.contains("rojo"), "El resultado debería contener a la criatura Alpha");
    }

    @Test
    void testTokensPorUsuario() {
        SolicitudDto dto = new SolicitudDto(List.of(1), List.of("beta"));

        //intentamos 2 peticiones del mismo usuario
        int t1 = SimulationService.solicitar("user1", dto);
        int t2 = SimulationService.solicitar("user1", dto);

        List<Integer> tokens = SimulationService.getTokenUsuario("user1");

        assertEquals(2, tokens.size(), "El usuario debería tener 2 tokens guardados");
        assertTrue(tokens.contains(t1));
        assertTrue(tokens.contains(t2));
    }

    @Test
    void testCreacionCriaturasInsensibleAMayusculas() {
        //mandamos critura ALPHA
        SolicitudDto dto = new SolicitudDto(List.of(1), List.of("ALPHA"));

        int token = SimulationService.solicitar("testUser", dto);
        String resultado = SimulationService.getResultado(token);

        assertTrue(resultado.contains("rojo"), "Debería haber reconocido a ALPHA aunque esté en mayúsculas");
    }
}
