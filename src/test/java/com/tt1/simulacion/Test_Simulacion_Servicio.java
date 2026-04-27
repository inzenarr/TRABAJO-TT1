package com.tt1.simulacion;

import com.tt1.simulacion.dto.SolicitudDto;
import com.tt1.simulacion.servicio.SimulationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Test_Simulacion_Servicio {

    @Test
    void testSolicitudYRecuperacionDeResultado() {
        String usuario = "ejemplo_recuperacion";
        SolicitudDto dto = new SolicitudDto(List.of(1), List.of("alpha"));

        int token = SimulationService.solicitar(usuario, dto);

        // Verificamos que el token sea un ID válido (mayor que 0)
        assertTrue(token > 0, "El token debería ser un número positivo");

        String resultado = SimulationService.getResultado(token);

        assertNotNull(resultado, "El resultado debería estar guardado en el mapa");
        // Según tu código, el ancho del tablero es 10, comprobamos que el String empieza bien
        assertTrue(resultado.startsWith("10"), "El resultado debe empezar con el tamaño del tablero");
        assertTrue(resultado.contains("rojo"), "El resultado debería contener el color rojo de Alpha");
    }

    @Test
    void testTokensPorUsuario() {
        String usuario = "ejemplo_tokens";
        SolicitudDto dto = new SolicitudDto(List.of(1), List.of("beta"));

        int tokensPrevios = SimulationService.getTokenUsuario(usuario).size();

        int t1 = SimulationService.solicitar(usuario, dto);
        int t2 = SimulationService.solicitar(usuario, dto);

        List<Integer> tokens = SimulationService.getTokenUsuario(usuario);

        assertEquals(tokensPrevios + 2, tokens.size(), "El usuario debería tener 2 tokens nuevos guardados");
        assertTrue(tokens.contains(t1), "La lista debería incluir el primer token");
        assertTrue(tokens.contains(t2), "La lista debería incluir el segundo token");
    }

    @Test
    void testCreacionCriaturasInsensibleAMayusculas() {
        SolicitudDto dto = new SolicitudDto(List.of(1, 1), List.of("ALPHA", "BeTa"));

        int token = SimulationService.solicitar("user_caps", dto);
        String resultado = SimulationService.getResultado(token);

        assertNotNull(resultado);
        assertTrue(resultado.contains("rojo"), "Debería haber reconocido ALPHA (rojo)");
        assertTrue(resultado.contains("azul"), "Debería haber reconocido BeTa (azul)");
    }

    @Test
    void testTokenInexistente() {
        String resultado = SimulationService.getResultado(99999);
        assertNull(resultado, "Si el token no existe, debería devolver null");
    }
}