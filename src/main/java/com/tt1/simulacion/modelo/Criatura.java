package com.tt1.simulacion.modelo;

/** Entidad base del modelo anémico. Solo almacena posición (x, y). */
public abstract class Criatura {

    protected final int x;
    protected final int y;

    protected Criatura(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
