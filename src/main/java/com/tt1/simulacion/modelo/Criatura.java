package com.tt1.simulacion.modelo;

/**
 * Clase base para todas las criaturas de la simulación.
 * Modelo anémico: solo contiene datos. El comportamiento reside en SimulacionService.
 */
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
