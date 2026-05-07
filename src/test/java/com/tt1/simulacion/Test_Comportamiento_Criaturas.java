package com.tt1.simulacion;

import com.tt1.simulacion.modelo.Alpha;
import com.tt1.simulacion.modelo.Beta;
import com.tt1.simulacion.modelo.Criatura;
import com.tt1.simulacion.modelo.Gamma;
import com.tt1.simulacion.servicio.SimulationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Test_Comportamiento_Criaturas {

    private final SimulationService service = new SimulationService();

    @Test
    void comportamientoAlpha() {
        Alpha alpha = new Alpha(3, 3);
        List<Criatura> resultado = service.handleComportamiento(alpha);

        assertEquals(1, resultado.size(), "Alpha solo debería devolver una criatura");
        assertEquals(3, resultado.get(0).getX());
        assertEquals(3, resultado.get(0).getY());
        assertInstanceOf(Alpha.class, resultado.get(0));
    }

    @Test
    void comportamientoBeta() {
        int xInicial = 5;
        int yInicial = 5;
        Beta beta = new Beta(xInicial, yInicial);

        List<Criatura> resultado = service.handleComportamiento(beta);
        Criatura movida = resultado.get(0);

        int distancia = Math.abs(movida.getX() - xInicial) + Math.abs(movida.getY() - yInicial);

        assertEquals(1, resultado.size());
        assertEquals(1, distancia, "Beta debería haberse movido exactamente a una casilla adyacente");
    }

    @Test
    void comportamientoGamma() {
        int xInicial = 5;
        int yInicial = 5;
        Gamma gamma = new Gamma(xInicial, yInicial);

        List<Criatura> resultado = service.handleComportamiento(gamma);

        assertTrue(resultado.size() == 1 || resultado.size() == 2,
                "Gamma debe devolver 1 (si falla la probabilidad) o 2 (si logra clonarse)");

        boolean originalSobrevive = resultado.stream()
                .anyMatch(c -> c.getX() == xInicial && c.getY() == yInicial);
        assertTrue(originalSobrevive, "La Gamma original debe permanecer en su posición inicial");

        if (resultado.size() == 2) {
            Criatura hijo = resultado.stream()
                    .filter(c -> c.getX() != xInicial || c.getY() != yInicial)
                    .findFirst()
                    .orElseThrow();

            assertInstanceOf(Gamma.class, hijo, "El clon debe ser tipo Gamma");

            int distancia = Math.abs(hijo.getX() - xInicial) + Math.abs(hijo.getY() - yInicial);
            assertEquals(1, distancia, "El clon debe haber aparecido a 1 sola casilla de distancia");
        }
    }
}