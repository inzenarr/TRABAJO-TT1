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

        assertTrue(!resultado.isEmpty() && resultado.size() <= 5, "Gamma debe devolver entre 1 (solo ella) y 5 (ella + 4 clones máximos) criaturas");

        boolean originalSobrevive = resultado.stream().anyMatch(c -> c.getX() == xInicial && c.getY() == yInicial);
        assertTrue(originalSobrevive, "La Gamma original debe permanecer en su posición inicial");

        for (Criatura c : resultado) {
            assertInstanceOf(Gamma.class, c, "Todas las criaturas devueltas deben ser de la clase Gamma");

            if (c.getX() != xInicial || c.getY() != yInicial) {
                int distancia = Math.abs(c.getX() - xInicial) + Math.abs(c.getY() - yInicial);
                assertEquals(1, distancia, "Cualquier clon debe haber aparecido exactamente a una casilla de distancia (adyacente)");
            }
        }
    }
}