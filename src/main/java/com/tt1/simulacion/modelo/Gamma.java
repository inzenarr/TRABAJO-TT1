package com.tt1.simulacion.modelo;

/** Criatura replicante. Se expande hacia una de las 4 posibles casillas adyacentes (1/4), con una probabilidad de desplazamiento de 1/5. Color: verde. */
public class Gamma extends Criatura {

    public Gamma(int x, int y) {
        super(x, y);
    }

    public Gamma(int x, int y, int hambre) {
        super(x, y, hambre);
    }
}
