package com.tt1.simulacion.modelo;

import java.util.List;

/**
 * Alpha: criatura estática. No se desplaza en ningún paso.
 */
public class Alpha extends Criatura {

    public Alpha(int x, int y) {
        super(x, y, "red");
    }

    @Override
    public List<Criatura> siguientePaso(int anchoTablero) {
        return List.of(new Alpha(x, y));
    }
}
