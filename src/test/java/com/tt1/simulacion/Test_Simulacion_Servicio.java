package com.tt1.simulacion;

import com.tt1.simulacion.dto.SolicitudDto;
import com.tt1.simulacion.servicio.SimulationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Test_Simulacion_Servicio {

    // Instancia del servicio para las pruebas unitarias
    private final SimulationService simulationService = new SimulationService();

    @Test
    void testSolicitudYRecuperacionDeResultado() {
        String usuario = "ejemplo_recuperacion";
        SolicitudDto dto = new SolicitudDto(List.of(1), List.of("alpha"));

        int token = simulationService.solicitar(usuario, dto);

        assertTrue(token > 0, "El token debería ser un número positivo");

        String resultado = simulationService.getResultado(token);

        assertNotNull(resultado, "El resultado debería estar guardado en el mapa de resultados");
        assertTrue(resultado.startsWith("10"), "El resultado debe empezar con el tamaño del tablero (10)");
        assertTrue(resultado.contains("rojo"), "El resultado debería contener el color rojo (propio de Alpha)");
    }

    @Test
    void testTokensAlmacenadosEnCliente() {
        String usuario = "cliente_test";
        SolicitudDto dto = new SolicitudDto(List.of(1), List.of("beta"));

        int tokensPrevios = simulationService.getTokenUsuario(usuario).size();

        int t1 = simulationService.solicitar(usuario, dto);
        int t2 = simulationService.solicitar(usuario, dto);

        List<Integer> tokens = simulationService.getTokenUsuario(usuario);

        assertNotNull(tokens, "La lista de tokens no debería ser nula");
        assertEquals(tokensPrevios + 2, tokens.size(), "El objeto Cliente debería haber almacenado 2 tokens nuevos");
        assertTrue(tokens.contains(t1), "La lista de tokens debe incluir el primer token generado");
        assertTrue(tokens.contains(t2), "La lista de tokens debe incluir el segundo token generado");
    }

    @Test
    void testCreacionCriaturasInsensibleAMayusculas() {
        SolicitudDto dto = new SolicitudDto(List.of(1, 1), List.of("ALPHA", "gAmMa"));

        int token = simulationService.solicitar("user_caps", dto);
        String resultado = simulationService.getResultado(token);

        assertNotNull(resultado, "La simulación debería haberse ejecutado");
        assertTrue(resultado.contains("rojo"), "Debería haber reconocido 'ALPHA' como rojo");
        assertTrue(resultado.contains("verde"), "Debería haber reconocido 'gAmMa' como verde");
    }

    @Test
    void testTokenInexistente() {
        String resultado = simulationService.getResultado(99999);
        assertNull(resultado, "Para un token inexistente, el resultado debe ser null");
    }
}