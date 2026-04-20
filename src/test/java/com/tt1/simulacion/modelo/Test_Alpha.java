package com.tt1.simulacion.modelo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Test_Alpha {
    @Test
    void coordenadasAlpha(){
        Alpha criaturaAlpha = new Alpha(3,4);
        assertEquals(3,criaturaAlpha.getX(),"Error en coordenda x");
        assertEquals(4,criaturaAlpha.getY(),"Error en coordenda y");
    }

    @Test
    void instanciaAlpha(){
        Alpha criaturaAlpha = new Alpha(3,4);
        assertInstanceOf(Criatura.class,criaturaAlpha,"Error al comprobar la instancia");
    }
}
