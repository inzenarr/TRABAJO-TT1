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
        boolean hijoObservado = false;

        // Con prob 1/5, en 100 intentos la probabilidad de no ver ningun hijo es (4/5)^100 ≈ 0.000002%
        for (int i = 0; i < 100; i++) {
            List<Criatura> resultado = service.handleComportamiento(gamma);

            // Solo puede devolver el padre o el padre + 1 hijo
            assertTrue(resultado.size() == 1 || resultado.size() == 2,
                    "Gamma debe devolver 1 o 2 criaturas");

            // El padre siempre permanece en su posición original
            boolean originalSobrevive = resultado.stream()
                    .anyMatch(c -> c.getX() == xInicial && c.getY() == yInicial);
            assertTrue(originalSobrevive, "La Gamma original debe permanecer en su posición inicial");

            if (resultado.size() == 2) {
                Criatura hijo = resultado.stream()
                        .filter(c -> c.getX() != xInicial || c.getY() != yInicial)
                        .findFirst()
                        .orElseThrow();

                assertInstanceOf(Gamma.class, hijo, "El hijo debe ser tipo Gamma");

                int distancia = Math.abs(hijo.getX() - xInicial) + Math.abs(hijo.getY() - yInicial);
                assertEquals(1, distancia, "El hijo debe estar exactamente a 1 casilla del padre");

                hijoObservado = true;
            }
        }

        assertTrue(hijoObservado, "En 100 intentos debería haberse generado al menos un hijo (prob 1/5)");
    }
}