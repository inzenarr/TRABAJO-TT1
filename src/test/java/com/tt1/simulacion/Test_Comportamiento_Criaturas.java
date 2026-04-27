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
        Gamma gamma = new Gamma(5, 5);
        List<Criatura> resultado = service.handleComportamiento(gamma);

        assertEquals(5, resultado.size(), "Gamma debería haberse duplicado en 4 direcciones más la original");

        boolean existeExpansion = resultado.stream().anyMatch(c -> c.getX() == 5 && c.getY() == 6);
        assertTrue(existeExpansion, "Debería existir una criatura Gamma en la posición expandida (5,6)");
    }
}
