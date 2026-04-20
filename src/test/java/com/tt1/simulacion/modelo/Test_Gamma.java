package com.tt1.simulacion.modelo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Test_Gamma {
    @Test
    void coordenadasGamma(){
        Gamma criaturaGamma = new Gamma(3,4);
        assertEquals(3,criaturaGamma.getX(),"Error en coordenda x");
        assertEquals(4,criaturaGamma.getY(),"Error en coordenda y");
    }

    @Test
    void instanciaGamma(){
        Gamma criaturaGamma = new Gamma(3,4);
        assertInstanceOf(Criatura.class,criaturaGamma,"Error al comprobar la instancia");
    }
}
