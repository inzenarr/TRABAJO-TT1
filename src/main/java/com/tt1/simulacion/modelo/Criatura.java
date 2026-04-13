package com.tt1.simulacion.modelo;

import java.util.List;

/**
 * Clase base para todas las criaturas de la simulación.
 * Cada subclase define su propio comportamiento en cada paso.
 */
public abstract class Criatura {

    protected final int x;
    protected final int y;
    protected final String color;

    protected Criatura(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    /**
     * Calcula el estado de esta criatura en el siguiente paso de tiempo.
     *
     * @param anchoTablero tamaño del tablero (se asume cuadrado)
     * @return lista de criaturas en el siguiente paso (puede ser 1 o más para Gamma)
     */
    public abstract List<Criatura> siguientePaso(int anchoTablero);

    public int getX() { return x; }
    public int getY() { return y; }
    public String getColor() { return color; }
}
