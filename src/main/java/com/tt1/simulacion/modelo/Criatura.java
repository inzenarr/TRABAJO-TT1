package com.tt1.simulacion.modelo;

/** Entidad base del modelo anémico. Almacena posición (x, y) y hambre. */
public abstract class Criatura {

    protected final int x;
    protected final int y;
    protected final int hambre;

    protected Criatura(int x, int y) {
        this(x, y, 0);
    }

    protected Criatura(int x, int y, int hambre) {
        this.x = x;
        this.y = y;
        this.hambre = hambre;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHambre() { return hambre; }
}
