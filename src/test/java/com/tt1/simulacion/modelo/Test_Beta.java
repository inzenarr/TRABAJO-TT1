package com.tt1.simulacion.modelo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Test_Beta {
    @Test
    void coordenadasBeta(){
        Beta criaturaBeta = new Beta(3,4);
        assertEquals(3,criaturaBeta.getX(),"Error en coordenda x");
        assertEquals(4,criaturaBeta.getY(),"Error en coordenda y");
    }

    @Test
    void instanciaBeta(){
        Beta criaturaBeta = new Beta(3,4);
        assertInstanceOf(Criatura.class,criaturaBeta,"Error al comprobar la instancia");
    }
}
